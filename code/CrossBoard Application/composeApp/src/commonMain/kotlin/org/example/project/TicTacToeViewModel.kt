package org.example.project

import domain.*
import httpModel.TicTacToeMoveInput
import httpModel.toMultiplayerMatch
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import util.Failure
import util.Success


data class TicTacToeUiState(
    val currentMatch: MultiPlayerMatch? = null,
    val userIdInput: String = "",
    val gameTypeInput: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
){
    val currentUserId: Int?
        get() = userIdInput.toIntOrNull()

    val currentUserToken: String
        get() = userIdInput
}


interface Clearable{
    fun clear()
}

class TicTacToeViewModel(
    private val client: MatchClient,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main
): Clearable {
    private val viewModelScope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val _uiState = MutableStateFlow(TicTacToeUiState())
    val uiState: StateFlow<TicTacToeUiState> = _uiState.asStateFlow()


    private var pollingJob: Job? = null

    fun updateUserIdInput(input: String){
        _uiState.update { it.copy(userIdInput = input, errorMessage = null) }
    }

    fun updateGameTypeInput(input: String){
        _uiState.update { it.copy(gameTypeInput = input, errorMessage = null) }
    }

    private suspend fun fetchMatchUpdates(matchId: Int): Boolean{
        val currentState = _uiState.value
        when(val result = client.getMatch(matchId)){
            is Success -> {
                val fetchedMatch = result.value
                val gameEndedOnServer = fetchedMatch.state != MatchState.RUNNING.toString()
                val gameAlreadyEndedInUi = currentState.currentMatch?.state != MatchState.RUNNING

                if (gameAlreadyEndedInUi && gameEndedOnServer){
                    _uiState.update { it.copy(currentMatch = fetchedMatch.toMultiplayerMatch()) }
                    return false
                }
                _uiState.update { it.copy(currentMatch = fetchedMatch.toMultiplayerMatch()) }

                return true
            }

            is Failure -> {
                _uiState.update { it.copy(errorMessage = result.value) }
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

    fun stopPolling(){
        pollingJob?.cancel()
        pollingJob = null
    }

    fun getMatchByVersion(matchId: Int, version: Int){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                when (val result = client.getMatchByVersion(matchId, version)) {
                    is Success -> {
                        _uiState.update { it.copy(isLoading = false, currentMatch = result.value.toMultiplayerMatch()) }
                    }
                    is Failure -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = result.value) }
                    }
                }
            }
            catch (e: Exception){
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: e.cause?.message) }
            }
        }
    }

    fun findMatch(){
        val currentState = _uiState.value
        if (currentState.currentUserToken.isBlank()){
            _uiState.update { it.copy(errorMessage = "Please enter a valid user Id") }
            return
        }

        if (currentState.gameTypeInput.isBlank()){
            _uiState.update { it.copy(errorMessage = "Please enter a valid gameType") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                when(val result = client.enterMatch(currentState.currentUserToken, currentState.gameTypeInput)){
                    is Success -> {
                        val match = result.value.toMultiplayerMatch()
                        _uiState.update { it.copy(isLoading = false, currentMatch = match) }

                        startPolling(result.value.matchId)
                    }

                    is Failure -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = result.value, currentMatch = null) }
                    }
                }
            }
            catch (e: Exception){
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: e.cause?.message) }
            }
        }
    }


    fun makeMove(rowIndex: Int, columnIndex: Int){
        val currentState = _uiState.value
        val match = currentState.currentMatch ?: return
        val userId = currentState.currentUserId ?: return
        val userToken = currentState.currentUserToken
        val board = match.board

        val playerType = match.getPlayerType(userId)

        if (board.turn != playerType){
            _uiState.update { it.copy(errorMessage = "Not your turn to play!") }
            return
        }

        val positionIndex = rowIndex * TicTacToeBoard.BOARD_DIM + columnIndex
        if (positionIndex >= board.positions.size || positionIndex < 0){
            _uiState.update { it.copy(errorMessage = "Invalid position Index: $positionIndex") }
            return
        }

        if ((board.positions[positionIndex] as TicPosition).player != Player.EMPTY){
            _uiState.update { it.copy(errorMessage = "This position is occupied!") }
            return
        }

        val rowNumber = TicTacToeBoard.BOARD_DIM - rowIndex
        val columnChar = 'a' + columnIndex

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
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
                        if (move == null) {
                            _uiState.update { it.copy(errorMessage = "There was an error in Move deserialization") }
                        }
                        else _uiState.update { it.copy(currentMatch = match.play(move)) }
                    }
                    is Failure -> {
                        _uiState.update { it.copy(errorMessage = result.value, isLoading = false) }
                    }
                }
            }
            catch (e: Exception){
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: e.cause?.message) }
            }
            finally {
                if(_uiState.value.isLoading){
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun forfeit(){
        val currentState = _uiState.value
        val match = currentState.currentMatch ?: return
        val userId = currentState.currentUserId ?: return
        val userToken = currentState.currentUserToken

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val forfeitedMatch = match.forfeit(userId)
                _uiState.update { it.copy(currentMatch = forfeitedMatch) }

                when(val result = client.forfeitMatch(userToken, match.id)){
                    is Success -> {
                        stopPolling()
                        _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                    }

                    is Failure -> {
                        _uiState.update { it.copy(errorMessage = result.value, isLoading = false, currentMatch = match) }
                    }
                }
            }
            catch (e: Exception){
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: e.cause?.message, currentMatch = match) }
            }
            finally {
                if(_uiState.value.isLoading){
                    _uiState.update { it.copy(isLoading = false) }
                }
            }

        }
    }
    fun resetMatch() {
        stopPolling()
        _uiState.update {
            it.copy(
                currentMatch = null,
                isLoading = false,
                errorMessage = null
            )
        }
    }

    override fun clear() {
        viewModelScope.cancel()
        stopPolling()
    }
}