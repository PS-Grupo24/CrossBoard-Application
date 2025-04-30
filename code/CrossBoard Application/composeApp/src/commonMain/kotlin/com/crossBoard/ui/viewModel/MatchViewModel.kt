package com.crossBoard.ui.viewModel

import androidx.lifecycle.ViewModel
import com.crossBoard.httpModel.TicTacToeMoveInput
import com.crossBoard.httpModel.toMultiplayerMatch
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import com.crossBoard.ApiClient
import com.crossBoard.domain.MatchState
import com.crossBoard.domain.Player
import com.crossBoard.domain.TicPosition
import com.crossBoard.domain.TicTacToeBoard
import com.crossBoard.domain.toMove
import com.crossBoard.model.MatchUiState
import com.crossBoard.util.Failure
import com.crossBoard.util.Success


interface Clearable{
    fun clear()
}

class MatchViewModel(
    private val client: ApiClient,
    private val userToken: String,
    private val currentUserId: Int,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
): Clearable, ViewModel() {

    /*
    companion object{
        fun factory(client: ApiClient, userToken: String, currentUserId: Int) = viewModelFactory {
            initializer {
                MatchViewModel(client, userToken, currentUserId)
            }
        }
    }*/
    private val viewModelScope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val _matchState = MutableStateFlow(MatchUiState())
    val matchState: StateFlow<MatchUiState> = _matchState.asStateFlow()


    private var pollingJob: Job? = null
    fun updateGameTypeInput(input: String){
        _matchState.update { it.copy(gameTypeInput = input, errorMessage = null) }
    }

    private suspend fun fetchMatchUpdates(matchId: Int): Boolean{
        val currentState = _matchState.value
        when(val result = client.getMatch(matchId)){
            is Success -> {
                val fetchedMatch = result.value
                val gameEndedOnServer =
                    fetchedMatch.state == MatchState.WIN.toString() || fetchedMatch.state == MatchState.DRAW.toString()
                val gameAlreadyEndedInUi =
                    currentState.currentMatch?.state == MatchState.WIN || currentState.currentMatch?.state == MatchState.DRAW

                if (gameAlreadyEndedInUi && gameEndedOnServer){
                    _matchState.update { it.copy(currentMatch = fetchedMatch.toMultiplayerMatch()) }
                    return false
                }
                if (currentState.currentMatch?.player2 == null && fetchedMatch.player2.userId != null)
                    getPlayersUsernames(null, fetchedMatch.player2.userId)
                _matchState.update { it.copy(currentMatch = fetchedMatch.toMultiplayerMatch()) }

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
                when(val result = client.enterMatch(userToken, currentState.gameTypeInput)){
                    is Success -> {
                        val match = result.value.toMultiplayerMatch()
                        _matchState.update { it.copy(isLoading = false, currentMatch = match) }
                        getPlayersUsernames(match?.player1, match?.player2)
                        startPolling(result.value.matchId)
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
            _matchState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                when(val result = client.cancelSearch(userToken, match.id)){
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
            _matchState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val forfeitedMatch = match.forfeit(currentUserId)
                _matchState.update { it.copy(currentMatch = forfeitedMatch) }

                when(val result = client.forfeitMatch(userToken, match.id)){
                    is Success -> {
                        stopPolling()
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

    override fun clear() {
        if (_matchState.value.currentMatch?.state == MatchState.WAITING) cancelSearch()
        if (_matchState.value.currentMatch?.state == MatchState.RUNNING) forfeit()
        stopPolling()
        viewModelScope.cancel()

    }
}