package com.crossBoard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.crossBoard.domain.*
import com.crossBoard.domain.board.Board
import com.crossBoard.domain.board.BoardWin
import com.crossBoard.domain.board.ReversiBoard
import com.crossBoard.domain.board.TicTacToeBoard
import com.crossBoard.model.PlayerInfo
import com.crossBoard.utils.CustomColor

/**
 * Screen responsible for the display of an ongoing or ended match.
 * @param match The current match.
 * @param currentUserId The id of the current logged user.
 * @param player1Username The username of player1.
 * @param player2Username The username of player2.
 * @param onCellClick The action to perform when a `Cell` is clicked.
 * @param onForfeitClick The action to perform when forfeit button is clicked.
 * @param isLoading The Loading state.
 * @param errorMessage The error message or `NULL` if there is none.
 * @param onPlayAgainClick The action to perform when play again button is clicked.
 * @param timeLeft The time left on the turn.
 */
@Composable
fun GameScreen(
    match: MultiPlayerMatch,
    currentUserId: Int?,
    player1Username: String,
    player2Username: String,
    onCellClick: (row: Int, col: Int) -> Unit,
    onForfeitClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onPlayAgainClick: () -> Unit,
    timeLeft: Int?
) {
    val board = match.board
    val isGameOver = match.state == MatchState.WIN || match.state == MatchState.DRAW

    val player1Symbol = when(match.matchType){
        MatchType.TicTacToe -> "X"
        MatchType.Reversi -> {
            if(match.board.player1 == Player.BLACK) "B" else "W"
        }
    }
    val player2Symbol = when(match.matchType){
        MatchType.TicTacToe ->"O"
        MatchType.Reversi -> {
            if(match.board.player2 == Player.BLACK) "B" else "W"
        }
    }

    val player1Type = remember(match.user1) { match.getPlayerType(match.user1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        MatchInfoPanel(
            matchId = match.id,
            currentUserId = currentUserId?: 0,
            PlayerInfo(match.user1, player1Username, player1Symbol),
            PlayerInfo(match.user2, player2Username, player2Symbol),
            timeLeft = timeLeft,
        )

        GameStatusAndBoard(
            board = board,
            match.state,
            player1Type = player1Type,
            player1Symbol = player1Symbol,
            player2Symbol = player2Symbol,
            isGameOver = isGameOver,
            isLoading = isLoading,
            onCellClick = onCellClick
        )

        GameActions(
            isLoading = isLoading,
            errorMessage = errorMessage,
            isGameOver = isGameOver,
            onForfeitClick = onForfeitClick,
            onPlayAgainClick = onPlayAgainClick
        )
    }
}

/**
 * Responsible for displaying the match, users and turn time information.
 * @param matchId The match id.
 * @param currentUserId The id of the logged user.
 * @param user1Info The information of the user1.
 * @param user2Info The information of the user2.
 * @param timeLeft The remaining time for each turn.
 */
@Composable
fun MatchInfoPanel(
    matchId: Int?,
    currentUserId: Int,
    user1Info: PlayerInfo,
    user2Info: PlayerInfo,
    timeLeft: Int?
){
    val me = if (currentUserId == user1Info.id) "Me: $user1Info" else "Me: $user2Info"
    val opponent = if (currentUserId == user1Info.id) "Opponent: $user2Info" else "Opponent: $user1Info"
    if(matchId != null){
        Text("Match ID: $matchId", style = MaterialTheme.typography.h6, color = CustomColor.LightBrown.value)
        Spacer(Modifier.height(8.dp))
    }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(me, style = MaterialTheme.typography.body1, color = CustomColor.LightBrown.value)

        if (timeLeft != null) {
            Text("$timeLeft", style = MaterialTheme.typography.body1, color = CustomColor.LightBrown.value)
        }

        val opponentText = if (user2Info.id == null) "Waiting..." else opponent
        Text(opponentText, style = MaterialTheme.typography.body1, color = CustomColor.LightBrown.value)
    }
}

/**
 * Responsible for displaying the board and its interactions.
 * It displays the current turn on the board when it is ongoing,
 * the winner when the match is over or that the match ended in a draw.
 * It is also responsible for calling the correct display for this type of match.
 * Example: `tictactoeBoardView` for the `MatchType.TicTacToe`
 * @param board The board to display.
 * @param state The current match state.
 * @param player1Type The type for player1.
 * @param player1Symbol The symbol to display for player1.
 * @param player2Symbol The symbol to display for player2.
 * @param isGameOver The flag that indicates if the current match is over.
 * @param isLoading The Loading state.
 * @param onCellClick The action to perform when a cell is clicked.
 */
@Composable
fun GameStatusAndBoard(
    board: Board,
    state: MatchState,
    player1Type: Player,
    player1Symbol: String,
    player2Symbol: String,
    isGameOver: Boolean,
    isLoading: Boolean,
    onCellClick: (row: Int, col: Int) -> Unit
) {
    val turnSymbol = if (board.turn == player1Type) player1Symbol else player2Symbol
    val status = when(state){
        MatchState.RUNNING -> "Turn: $turnSymbol"
        MatchState.WIN -> {
            val winner = if ((board as BoardWin).winner == player1Type) player1Symbol else player2Symbol
            "Winner: $winner"
        }
        MatchState.DRAW -> "Draw"
        else -> "ILLEGAL STATE"
    }

    Text(status, style = MaterialTheme.typography.h5,  color = CustomColor.LightBrown.value)
    Spacer(Modifier.height(16.dp))
    when(board) {
        is TicTacToeBoard -> {
            ticTacToeBoardView(
                board = board,
                onCellClick = { row, col -> if (!isGameOver) onCellClick(row, col) },
                enabled = !isLoading && !isGameOver,
                player1Type = player1Type,
                player1Symbol = player1Symbol,
                player2Symbol = player2Symbol,
            )
        }
        is ReversiBoard -> {
            reversiBoardView(
                board = board,
                onClick = { row: Int, col: Int -> if (!isGameOver) onCellClick(row, col) },
                enabled = !isLoading && !isGameOver,
                player1Type = player1Type,
                player1Symbol = player1Symbol,
                player2Symbol = player2Symbol
            )
        }
        else -> throw(IllegalArgumentException("Illegal Board Type: ${board::class.simpleName}"))
    }
}

/**
 * Responsible for the extra match functionalities such as forfeiting when the match is ongoing
 * or play again when the match is ended.
 * If the forfeit button is clicked, an alert dialog will be used to confirm this action.
 * @param isLoading The loading state.
 * @param errorMessage The current error message: `NULL` if there is none.
 * @param isGameOver The flag that indicates if a match is over.
 * @param onForfeitClick The action to perform when the forfeit button is clicked.
 * @param onPlayAgainClick The action to perform when the play again button is clicked.
 */
@Composable
fun GameActions(
    isLoading: Boolean,
    errorMessage: String?,
    isGameOver: Boolean,
    onForfeitClick: () -> Unit,
    onPlayAgainClick: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        MyAlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = "Confirm Forfeit",
            text = "Are you sure you want to forfeit the match?",
            onConfirm = {
                showConfirmDialog = false
                onForfeitClick()
            },
            confirmText = "Yes, Forfeit",
            onDismiss = {showConfirmDialog = false},
            dismissText = "Cancel",
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val elementHeight = 48.dp
        if (isLoading) {
            Box(modifier = Modifier.height(elementHeight)){
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else {
            errorMessage?.let {
                Box(modifier = Modifier.height(elementHeight)){
                    Text(
                        text = it,
                        color = MaterialTheme.colors.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp).align(Alignment.Center)
                    )
                }
            }
        }

        Box(modifier = Modifier.height(elementHeight)) {
            if (isGameOver) {

                Button(
                    onClick = onPlayAgainClick,
                    modifier = Modifier.align(Alignment.Center),
                    colors = ButtonDefaults.buttonColors(backgroundColor = CustomColor.DarkBrown.value)
                ) {
                    Text("Play Again", color = Color.White)
                }
            } else {
                Button(
                    onClick = { showConfirmDialog = true },
                    enabled = !isLoading ,
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("Forfeit Match")
                }
            }
        }
    }
}