package crossBoard.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import crossBoard.domain.*

data class PlayerInfo(
    val id: Int?,
    val username: String,
    val symbol: String,

){
    override fun toString(): String {
        return "$username->($symbol)"
    }
}

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
    onPlayAgainClick: () -> Unit
) {
    val board = match.board
    val isGameOver = match.state == MatchState.WIN || match.state == MatchState.DRAW

    val player1Symbol = "X"
    val player2Symbol = "O"

    val player1Type = remember(match.player1) { match.getPlayerType(match.player1) }

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
            PlayerInfo(match.player1, player1Username, player1Symbol),
            PlayerInfo(match.player2, player2Username, player2Symbol)
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

@Composable
fun MatchInfoPanel(
    matchId: Int,
    currentUserId: Int,
    user1Info: PlayerInfo,
    user2Info: PlayerInfo,
){
    val me = if (currentUserId == user1Info.id) "Me: $user1Info" else "Me: $user2Info"
    val opponent = if (currentUserId == user1Info.id) "Opponent: $user2Info" else "Opponent: $user1Info"
    Text("Match ID: $matchId", style = MaterialTheme.typography.h6)
    Spacer(Modifier.height(8.dp))
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(me, style = MaterialTheme.typography.body1)
        val opponentText = if (user2Info.id == null) "Waiting..." else opponent
        Text(opponentText, style = MaterialTheme.typography.body1)
    }
}

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
        MatchState.WAITING -> "Waiting for Opponent"
        else -> "Unknown State"
    }

    Text(status, style = MaterialTheme.typography.h5)
    Spacer(Modifier.height(16.dp))
    ticTacToeBoardView(
        board = board,
        onCellClick = { row, col -> if (!isGameOver) onCellClick(row, col) },
        enabled = !isLoading && !isGameOver,
        player1Type = player1Type,
        player1Symbol = player1Symbol,
        player2Symbol = player2Symbol,
    )
}

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
        AlertDialog(
            onDismissRequest = {
                showConfirmDialog = false
            },
            title = {
                Text("Confirm Forfeit")
            },
            text = {
                Text("Are you sure you want to forfeit the match?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        onForfeitClick()
                    }
                ) {
                    Text("Yes, Forfeit")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val elementHeight = 48.dp

        Box(modifier = Modifier.height(elementHeight)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                errorMessage?.let {
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
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("Play Again")
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