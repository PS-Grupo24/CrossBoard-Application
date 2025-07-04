package com.crossBoard.ui.viewModel

import com.crossBoard.httpModel.TicTacToeMoveInput

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import com.crossBoard.ApiClient
import com.crossBoard.domain.*
import com.crossBoard.domain.board.ReversiBoard
import com.crossBoard.domain.board.TicTacToeBoard
import com.crossBoard.domain.move.toMove
import com.crossBoard.domain.position.TicPosition
import com.crossBoard.httpModel.MoveInput
import com.crossBoard.httpModel.ReversiMoveInput
import com.crossBoard.interfaces.Clearable
import com.crossBoard.model.MultiplayerMatchUiState
import com.crossBoard.util.Failure
import com.crossBoard.util.Success

/**
 * ViewModel responsible for managing the MultiPlayerMatch resources.
 * Uses `MultiPlayerMatchUiState` to manage the match resources.
 * @param client The `APIClient` responsible for the server requests.
 * @param userToken The token of the logged user.
 * @param currentUserId The id of the logged user.
 * @param mainDispatcher The coroutine dispatcher; `Dispatchers.Main` by default.
 */
class MultiplayerMatchViewModel(
    private val client: ApiClient,
    private val userToken: String,
    private val currentUserId: Int,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
): Clearable {

    private val viewModelScope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val _matchState = MutableStateFlow(MultiplayerMatchUiState())
    val matchState: StateFlow<MultiplayerMatchUiState> = _matchState.asStateFlow()

    /**
     * Job for the sse update collection.
     */
    private var sseJob: Job? = null

    /**
     * Job for the turn timer countdown.
     */
    private var timerJob: Job? = null

    /**
     * Function "startTurnTimer" responsible for creating a Job that counts down starting at a given duration.
     * @param durationSeconds The duration in seconds for each turn.
     */
    private fun startTurnTimer(durationSeconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var timeLeft = durationSeconds
            while (timeLeft >= 0) {
                _matchState.update { it.copy(timeLeftSeconds = timeLeft) }
                delay(1000)
                timeLeft--
            }
        }
    }

    /**
     * Function that cancels the timer job.
     */
    private fun stopTurnTimer() {
        timerJob?.cancel()
        _matchState.update { it.copy(timeLeftSeconds = 0) }
    }

    /**
     * Function "startSSE" that launches the job for SSE update collection.
     */
    private fun startSSE(){

        if (sseJob?.isActive == true) {
            println("VM: SSE collecting job already active.")
            return
        }
        sseJob = viewModelScope.launch {
            try {
                client.connectSSE(userToken)
                    .onStart {
                        println("Starting sse")
                    }
                    .onEach { matchUpdate ->
                        val currentMatch = _matchState.value.currentMatch
                        _matchState.update{ it.copy(currentMatch = matchUpdate) }
                        if (currentMatch?.version == 1 && matchUpdate.version == 2){
                            viewModelScope.launch { getPlayersUsernames(matchUpdate.user1, matchUpdate.user2) }
                        }
                        if (matchUpdate.state == MatchState.WIN || matchUpdate.state == MatchState.DRAW){
                            disconnectSSE()
                        }
                    }
                    .catch { cause ->
                        println("VM: SSE Flow caught error: ${cause.message}")
                        viewModelScope.launch {
                            println("VM: Attempting SSE reconnection in 5s...")
                            delay(5000)
                            startSSE()
                        }
                        sseJob = null
                    }
                    .onCompletion {
                        sseJob = null
                    }
                    .collect()
            }
            catch (_: Exception) {
                sseJob = null
            }

        }

    }

    /**
     * Function "disconnectSSE" responsible for canceling the sseJob.
     */
    fun disconnectSSE(){
        println("VM: Disconnecting SSE...")
        sseJob?.cancel()
        sseJob = null
    }

    /**
     * Function "updateMatchTypeInput" responsible for updating the type of the match to start.
     */
    fun updateMatchTypeInput(input: String){
        _matchState.update { it.copy(gameTypeInput = input, errorMessage = null) }
    }

    /**
     * Function "findMatch" responsible for using finding a match, calling `getPlayersUsernames` and `startSSE`.
     */
    fun findMatch(){
        val currentState = _matchState.value
        if (userToken.isBlank()){
            _matchState.update { it.copy(errorMessage = "Please enter a valid user") }
            return
        }

        if (currentState.gameTypeInput.isBlank()){
            _matchState.update { it.copy(errorMessage = "Please enter a valid gameType") }
            return
        }

        viewModelScope.launch {
            _matchState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                when(val result = client.enterMatch(userToken, currentState.gameTypeInput)){
                    is Success -> {
                        val match = result.value
                        _matchState.update { it.copy(isLoading = false, currentMatch = match) }
                        if (match.state == MatchState.RUNNING) { startTurnTimer(30)}
                        getPlayersUsernames(match.user1, match.user2)
                        startSSE()
                    }

                    is Failure -> {
                        _matchState.update { it.copy(isLoading = false, errorMessage = result.value, currentMatch = null) }
                    }
                }
            }
            catch (e: Exception){
                _matchState.update { it.copy(isLoading = false, errorMessage = e.message ?: e.cause?.message) }
            }
        }
    }

    /**
     * Function `cancelSearch` responsible for canceling a match.
     */
    fun cancelSearch(){
        val currentState = _matchState.value
        val match = currentState.currentMatch ?: return
        if (match.state != MatchState.WAITING) return

        viewModelScope.launch {
            doCancelSearch(match.id)
        }
    }

    /**
     * Function "makeMove" responsible for creating a move and performing it given the row index and column index.
     * @param rowIndex The index of the row.
     * @param columnIndex The index of the column.
     */
    fun makeMove(rowIndex: Int, columnIndex: Int){
        val currentState = _matchState.value
        val match = currentState.currentMatch ?: return
        val board = match.board

        val playerType = match.getPlayerType(currentUserId)

        if (match.user2 == null){
            _matchState.update { it.copy(errorMessage = "Please wait for a player to join") }
            return
        }
        if (board.turn != playerType ){
            _matchState.update { it.copy(errorMessage = "Not your turn to play!") }
            return
        }

        val square = when(match.matchType){
            MatchType.TicTacToe -> Square(Row(rowIndex, TicTacToeBoard.BOARD_DIM), Column('a' + columnIndex))
            MatchType.Reversi -> Square(Row(rowIndex, ReversiBoard.BOARD_DIM), Column('a' + columnIndex))
            else -> {
                _matchState.update { it.copy(errorMessage = "Unsupported match type: ${match.matchType}") }
                return
            }
        }

        if (match.board.get(square) != Player.EMPTY){
            _matchState.update { it.copy(errorMessage = "Invalid move! Cell already occupied.") }
            return
        }

        viewModelScope.launch {
            _matchState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val moveInput = getMoveInput(match.matchType, playerType, square.row.number, square.column.symbol)
                
                when(val result = client.playMatch(
                    userToken,
                    match.id,
                    match.version,
                    moveInput
                )
                ){
                    is Success -> {
                        val move = result.value.move.toMove()
                        val newMatch = match.play(move)
                        _matchState.update { it.copy(currentMatch = newMatch) }
                        startTurnTimer(30)
                    }
                    is Failure -> {
                        _matchState.update { it.copy(errorMessage = result.value, isLoading = false) }
                    }
                }
            }
            catch (e: Exception){
                _matchState.update { it.copy(isLoading = false, errorMessage = e.message ?: e.cause?.message) }
            }
            finally {
                if(_matchState.value.isLoading){
                    _matchState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    /**
     * Function "forfeit" responsible for forfeiting the match.
     */
    fun forfeit(){
        val currentState = _matchState.value
        val match = currentState.currentMatch ?: return

        viewModelScope.launch {
            doForfeit(match)
        }
    }

    /**
     * Function "resetMatch" responsible for clearing the match resources and disconnect the SSE updates.
     */
    fun resetMatch() {
        _matchState.update {
            it.copy(
                currentMatch = null,
                isLoading = false,
                errorMessage = null,
            )
        }
        disconnectSSE()
    }

    /**
     * Auxiliary function that gets the username information for the users.
     */
    private fun getPlayersUsernames(player1Id:Int?, player2Id: Int?){

        viewModelScope.launch{
            if (player1Id == null && player2Id == null) return@launch
            _matchState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                if (player1Id != null){
                    when(val player1Result = client.getUserById(player1Id)) {
                        is Success -> {
                            _matchState.update { it.copy(player1Username = player1Result.value.username.value, isLoading = false) }
                        }
                        is Failure -> {
                            _matchState.update { it.copy(isLoading = false, errorMessage = player1Result.value) }
                        }
                    }
                }

                if (player2Id != null){
                    when(val player2Result = client.getUserById(player2Id)) {
                        is Success -> {
                            _matchState.update { it.copy(player2Username = player2Result.value.username.value, isLoading = false) }
                        }
                        is Failure -> {
                            _matchState.update { it.copy(isLoading = false, errorMessage = player2Result.value) }
                        }
                    }
                }

            }
            catch (e: Exception){
                _matchState.update { it.copy(isLoading = false, errorMessage = e.message ?: e.cause?.message) }
            }
        }
    }

    /**
     * Auxiliary function that performs a match cancel.
     */
    private suspend fun doCancelSearch(matchId: Int){
        _matchState.update { it.copy(isLoading = true, errorMessage = null) }

        try {
            when(val result = client.cancelSearch(userToken,matchId)){
                is Success -> {
                    resetMatch()

                }
                is Failure -> {
                    _matchState.update { it.copy(isLoading = false, errorMessage = result.value) }
                }
            }
        }
        catch (e: Exception){
            _matchState.update { it.copy(isLoading = false, errorMessage = e.message ?: e.cause?.message) }
        }
    }

    /**
     * Auxiliary function that performs a match forfeit.
     */
    private suspend fun doForfeit(match: MultiPlayerMatch){
        _matchState.update { it.copy(isLoading = true, errorMessage = null) }

        try {
            val forfeitedMatch = match.forfeit(currentUserId)
            _matchState.update { it.copy(currentMatch = forfeitedMatch) }

            when(val result = client.forfeitMatch(userToken, match.id)){
                is Success -> {
                    disconnectSSE()
                    stopTurnTimer()
                    _matchState.update { it.copy(isLoading = false, errorMessage = null) }
                }

                is Failure -> {
                    _matchState.update { it.copy(errorMessage = result.value, isLoading = false, currentMatch = match) }
                }
            }
        }
        catch (e: Exception){
            _matchState.update { it.copy(isLoading = false, errorMessage = e.message ?: e.cause?.message, currentMatch = match) }
        }
        finally {
            if(_matchState.value.isLoading){
                _matchState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Function "clear" responsible for performing a cleanup of the viewModel when closed.
     * It cancels the match if it's still WAITING for an opponent or forfeits if the match is ongoing
     * and cancels the viewModel scope.
     *
     */
    override fun clear() {
        val match = _matchState.value.currentMatch
        val state = match?.state

        CoroutineScope(Dispatchers.Default).launch {
            if (match != null && state == MatchState.WAITING ) doCancelSearch(match.id)
            if (state == MatchState.RUNNING) doForfeit(match)
        }
        viewModelScope.cancel()
    }
}

private fun getMoveInput(matchType: MatchType, playerType: Player, rowNumber: Int, columnChar: Char): MoveInput {
    when(matchType) {
        MatchType.TicTacToe -> {
            return TicTacToeMoveInput(
                playerType.toString(),
                "$rowNumber$columnChar"
            )
        }
        MatchType.Reversi -> {
            return ReversiMoveInput(
                playerType.toString(),
                "$rowNumber$columnChar"
            )
        }
        else -> throw IllegalArgumentException("Unsupported match type: $matchType")
    }
}