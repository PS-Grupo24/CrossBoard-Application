package com.crossBoard.ui.viewModel

import com.crossBoard.httpModel.TicTacToeMoveInput
import com.crossBoard.httpModel.toMultiplayerMatch
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import com.crossBoard.ApiClient
import com.crossBoard.domain.*
import com.crossBoard.interfaces.Clearable
import com.crossBoard.model.MultiplayerMatchUiState
import com.crossBoard.util.Failure
import com.crossBoard.util.Success
import io.ktor.websocket.*


class MultiplayerMatchViewModel(
    private val client: ApiClient,
    private val userToken: String,
    private val currentUserId: Int,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
): Clearable {

    private val viewModelScope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val _matchState = MutableStateFlow(MultiplayerMatchUiState())
    val matchState: StateFlow<MultiplayerMatchUiState> = _matchState.asStateFlow()


    private var pollingJob: Job? = null
    private var timerJob: Job? = null

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

    private fun stopTurnTimer() {
        timerJob?.cancel()
        _matchState.update { it.copy(timeLeftSeconds = 0) }
    }

    fun connectToWebsocket() {
        viewModelScope.launch {
            client.connectGameWebSocket(currentUserId, userToken)
                .collect { frame -> handleWebSocketMessage(frame) }
        }
    }

    private fun handleWebSocketMessage(frame: Frame){
        if (frame is Frame.Text){
            val message = frame.readText()
            _matchState.update{
                it.copy(incomingWebSocketErrorMessage = it.incomingWebSocketErrorMessage + message)
            }
        }
    }

    private fun sendMessageToWebSocket(message: String){
        viewModelScope.launch {
            try {
                val frame = Frame.Text(message)
                client.sendGameWebSocketMessage(frame)
            }
            catch (e: Exception){
                _matchState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    private fun disconnectFromWebSocket(){
        viewModelScope.launch {
            client.disconnectGameWebSocket()
        }
    }


    fun updateGameTypeInput(input: String){
        _matchState.update { it.copy(gameTypeInput = input, errorMessage = null) }
    }

    private suspend fun fetchMatchUpdates(matchId: Int): Boolean{
        val currentMatch = _matchState.value
        when(val result = client.getMatch(matchId)){
            is Success -> {
                val fetchedMatch = result.value.toMultiplayerMatch()
                val gameEndedOnServer =
                    fetchedMatch?.state == MatchState.WIN || fetchedMatch?.state == MatchState.DRAW
                val gameAlreadyEndedInUi =
                    currentMatch.currentMatch?.state == MatchState.WIN || currentMatch.currentMatch?.state == MatchState.DRAW

                if (gameAlreadyEndedInUi && gameEndedOnServer){
                    _matchState.update { it.copy(currentMatch = fetchedMatch) }
                    stopPolling()
                    stopTurnTimer()
                    return false
                }

                if(
                    fetchedMatch?.version != currentMatch.currentMatch?.version
                    && !gameAlreadyEndedInUi
                    && !gameEndedOnServer
                ) startTurnTimer(30)

                if (currentMatch.currentMatch?.player2 == null && fetchedMatch?.player2 != null)
                    getPlayersUsernames(null, fetchedMatch.player2)

                _matchState.update { it.copy(currentMatch = fetchedMatch) }

                if(currentMatch.currentMatch?.state == MatchState.WAITING && fetchedMatch?.state == MatchState.RUNNING)
                    startTurnTimer(30)

                return true
            }

            is Failure -> {
                _matchState.update { it.copy(errorMessage = result.value) }
                return true
            }
        }
    }

    private fun startPolling(matchId: Int) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while(isActive){
                if (!fetchMatchUpdates(matchId)) break
                delay(1500)
            }
        }
    }

    private fun stopPolling(){
        pollingJob?.cancel()
        pollingJob = null
    }

    fun getMatchByVersion(matchId: Int, version: Int){
        viewModelScope.launch {
            _matchState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                when (val result = client.getMatchByVersion(matchId, version)) {
                    is Success -> {
                        _matchState.update { it.copy(isLoading = false, currentMatch = result.value.toMultiplayerMatch()) }
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
    }

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
                println("TOKEN: $userToken")
                when(val result = client.enterMatch(userToken, currentState.gameTypeInput)){
                    is Success -> {
                        val match = result.value.toMultiplayerMatch()
                        _matchState.update { it.copy(isLoading = false, currentMatch = match) }
                        if (match?.state == MatchState.RUNNING) { startTurnTimer(30)}
                        getPlayersUsernames(match?.player1, match?.player2)
                        startPolling(result.value.matchId)
                        connectToWebsocket()
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

    fun cancelSearch(){
        val currentState = _matchState.value
        val match = currentState.currentMatch ?: return
        if (match.state != MatchState.WAITING) return

        viewModelScope.launch {
            doCancelSearch(match.id)
        }
    }
    fun makeMove(rowIndex: Int, columnIndex: Int){
        val currentState = _matchState.value
        val match = currentState.currentMatch ?: return
        val board = match.board

        val playerType = match.getPlayerType(currentUserId)

        if (match.player2 == null){
            _matchState.update { it.copy(errorMessage = "Please wait for a player to join") }
            return
        }
        if (board.turn != playerType ){
            _matchState.update { it.copy(errorMessage = "Not your turn to play!") }
            return
        }

        val positionIndex = rowIndex * TicTacToeBoard.BOARD_DIM + columnIndex
        if (positionIndex >= board.positions.size || positionIndex < 0){
            _matchState.update { it.copy(errorMessage = "Invalid position Index: $positionIndex") }
            return
        }

        if ((board.positions[positionIndex] as TicPosition).player != Player.EMPTY){
            _matchState.update { it.copy(errorMessage = "This position is occupied!") }
            return
        }

        val rowNumber = TicTacToeBoard.BOARD_DIM - rowIndex
        val columnChar = 'a' + columnIndex

        viewModelScope.launch {
            _matchState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val moveInput = TicTacToeMoveInput(
                    playerType.toString(),
                    "$rowNumber$columnChar",
                )
                when(val result = client.playMatch(
                    userToken,
                    match.id,
                    match.version,
                    moveInput
                )
                ){
                    is Success -> {
                        val move = result.value.move.toMove()
                        _matchState.update { it.copy(currentMatch = match.play(move)) }
                        startTurnTimer(30)
                        val webSocketMessage = "Player ${playerType.name} made a move at position $rowNumber$columnChar"
                        sendMessageToWebSocket(webSocketMessage)
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

    fun forfeit(){
        val currentState = _matchState.value
        val match = currentState.currentMatch ?: return

        viewModelScope.launch {
            doForfeit(match)
        }
    }
    fun resetMatch() {
        stopPolling()
        _matchState.update {
            it.copy(
                currentMatch = null,
                isLoading = false,
                errorMessage = null
            )
        }
        disconnectFromWebSocket()
    }

    private fun getPlayersUsernames(player1Id:Int?, player2Id: Int?){

        viewModelScope.launch{
            if (player1Id == null && player2Id == null) return@launch
            _matchState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                if (player1Id != null){
                    when(val player1Result = client.getUserById(player1Id)) {
                        is Success -> {
                            _matchState.update { it.copy(player1Username = player1Result.value.username, isLoading = false) }
                        }
                        is Failure -> {
                            _matchState.update { it.copy(isLoading = false, errorMessage = player1Result.value) }
                        }
                    }
                }

                if (player2Id != null){
                    when(val player2Result = client.getUserById(player2Id)) {
                        is Success -> {
                            _matchState.update { it.copy(player2Username = player2Result.value.username, isLoading = false) }
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

    private suspend fun doForfeit(match: MultiPlayerMatch){
        _matchState.update { it.copy(isLoading = true, errorMessage = null) }

        try {
            val forfeitedMatch = match.forfeit(currentUserId)
            _matchState.update { it.copy(currentMatch = forfeitedMatch) }

            when(val result = client.forfeitMatch(userToken, match.id)){
                is Success -> {
                    val webSocketMessage = "User $currentUserId has forfeited the game"
                    sendMessageToWebSocket(webSocketMessage)
                    stopPolling()
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