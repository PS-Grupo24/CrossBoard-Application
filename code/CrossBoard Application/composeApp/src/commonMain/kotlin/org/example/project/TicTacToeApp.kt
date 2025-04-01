package org.example.project

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.*
import domain.Player
import domain.TicTacToeBoard
import httpModel.MatchOutput
import kotlinx.coroutines.launch
import util.Failure
import util.Success


@Composable
fun ticTacToeApp(client: MatchClient) {
    var currentMatch by remember { mutableStateOf<MatchOutput?>(null) }
    var userIdInput by remember { mutableStateOf("") }
    var gameTypeInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val currentUserId: Int? = userIdInput.toIntOrNull()

    fun findMatch(){
        val userId = userIdInput.toIntOrNull()
        if (userId == null) {
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


            when (val result = client.enterMatch(userId, gameTypeInput)) {
                is Success -> { currentMatch = result.value}
                is Failure -> {errorMessage = result.value}
            }
            isLoading = false
        }
    }

    fun makeMove(rowIndex: Int, columnIndex: Int) {
        val match = currentMatch ?: return
        val userId = currentUserId ?: return

        val board = match.board

        val playerType = when(userId){
            match.player1.userId -> match.player1.playerType
            match.player2.userId -> match.player2.playerType
            else -> {
                errorMessage = "You are not a player"
                return
            }
        }

        if (board.turn != playerType) {
            errorMessage = "Not your turn"
            return
        }

        val positionIndex = rowIndex * 3 + columnIndex
        if (positionIndex > board.positions.size) {
            errorMessage = "Invalid position"
            return
        }
        if(board.positions[positionIndex] != Player.EMPTY.toString()) {
            errorMessage = "this position is already occupied"
            return
        }

        val rowNumber = TicTacToeBoard.BOARD_DIM - rowIndex
        val columnChar = 'a' + columnIndex

        scope.launch {
            isLoading = true
            errorMessage = null

            when(val result = client.playMatch(
                userId,
                match.matchId,
                playerType,
                rowNumber,
                columnChar
            )){
                is Success -> {
                    currentMatch = result.value
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
            when(val result = client.forfeitMatch(userId, match.matchId)){
                is Success -> {
                    currentMatch = result.value
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
                onFindMatchClick = {::findMatch},
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