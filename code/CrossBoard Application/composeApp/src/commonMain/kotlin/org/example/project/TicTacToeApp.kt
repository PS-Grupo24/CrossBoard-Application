package org.example.project

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.*
import domain.*
import httpModel.toMultiplayerMatch
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import util.Failure
import util.Success


@Composable
fun ticTacToeApp(client: MatchClient) {
    var currentMatch by remember { mutableStateOf<MultiPlayerMatch?>(null) }
    var userIdInput by remember { mutableStateOf("") }
    var gameTypeInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val currentUserId = userIdInput.toIntOrNull()
    val currentUserToken = userIdInput

    suspend fun fetchMatchUpdates(matchId: Int): Boolean {
            when (val result = client.getMatch(matchId)) {
                is Success -> {
                    if (result.value.board.state != RUNNING_STATE && currentMatch?.board !is BoardRun) {
                        return false
                    }
                    currentMatch = result.value.toMultiplayerMatch()
                    return true
                }
                is Failure -> {
                    errorMessage = result.value
                    return true
                }
            }
    }
    fun startPolling(matchId: Int) {
        scope.launch {
            while (true) {
                delay(3000)
                if (!fetchMatchUpdates(matchId)) break
            }
        }
    }

    fun getMatchByVersion(matchId: Int, version: Int){
        scope.launch {
            isLoading = true
            errorMessage = null
            when (val result = client.getMatchByVersion(matchId, version)) {
                is Success -> {
                    currentMatch = result.value.toMultiplayerMatch()
                }
                is Failure -> {
                    errorMessage = result.value
                }
            }
        }
    }

    fun findMatch(){
        if (currentUserToken == null) {
            errorMessage = "Please enter a valid user id"
            return
        }
        if(gameTypeInput.isBlank()) {
            errorMessage = "Please enter a game type"
            return
        }
        scope.launch {
            isLoading = true
            errorMessage = null


            when (val result = client.enterMatch(currentUserToken, gameTypeInput)) {
                is Success -> {
                    currentMatch = result.value.toMultiplayerMatch()
                    startPolling(result.value.matchId)
                }
                is Failure -> {errorMessage = result.value}
            }
            isLoading = false
        }
    }

    fun makeMove(rowIndex: Int, columnIndex: Int) {
        val match = currentMatch ?: return
        val userId = currentUserId ?: return
        val userToken = currentUserToken ?: return
        val board = match.board

        val playerType = match.getPlayerType(userId)

        if (board.turn != playerType) {
            errorMessage = "Not your turn"
            return
        }

        val positionIndex = rowIndex * 3 + columnIndex
        if (positionIndex > board.positions.size) {
            errorMessage = "Invalid position"
            return
        }
        if(board.positions[positionIndex].player != Player.EMPTY) {
            errorMessage = "this position is already occupied"
            return
        }

        val rowNumber = TicTacToeBoard.BOARD_DIM - rowIndex
        val columnChar = 'a' + columnIndex

        scope.launch {
            isLoading = true
            errorMessage = null

            when(val result = client.playMatch(
                userToken,
                match.id,
                match.version,
                playerType.toString(),
                rowNumber,
                columnChar
            )){
                is Success -> {
                    val move = result.value.move.toMove()
                    if (move == null) {
                        errorMessage = "There was an error with the move conversion"
                        return@launch
                    }
                    currentMatch = match.play(move)
                }
                is Failure -> {
                    errorMessage = result.value
                }
            }

            isLoading = false

        }
    }

    fun forfeit(){
        val match = currentMatch ?: return
        val userId = currentUserId ?: return
        scope.launch {
            isLoading = true
            errorMessage = null
            when(val result = client.forfeitMatch(currentUserToken, match.id)){
                is Success -> {
                    currentMatch = match.forfeit(userId)
                }
                is Failure -> {
                    errorMessage = result.value
                }
            }
            isLoading = false
        }
    }

    AnimatedContent(targetState = currentMatch){ match ->
        if (match == null){
            FindMatchScreen(
                userIdInput,
                onUserIdChange = {userIdInput = it},
                gameType = gameTypeInput,
                onGameTypeChange = {gameTypeInput = it},
                onFindMatchClick = ::findMatch,
                isLoading = isLoading,
                errorMessage = errorMessage
            )
        }
        else{
            GameScreen(
                match = match,
                currentUserId = currentUserId,
                onCellClick = ::makeMove,
                onForfeitClick = ::forfeit,
                isLoading = isLoading,
                errorMessage = errorMessage,
                onPlayAgainClick = {currentMatch = null },
            )
        }
    }
}