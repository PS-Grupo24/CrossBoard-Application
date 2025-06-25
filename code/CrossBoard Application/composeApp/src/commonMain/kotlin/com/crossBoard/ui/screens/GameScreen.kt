package com.crossBoard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.crossBoard.domain.board.Board
import com.crossBoard.domain.board.BoardWin
import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MultiPlayerMatch
import com.crossBoard.domain.Player
import com.crossBoard.model.PlayerInfo
import com.crossBoard.utils.CustomColor

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
    webSocketMessage: String?,
    onPlayAgainClick: () -> Unit,
    timeLeft: Int?
) {
    val board = match.board
    val isGameOver = match.state == MatchState.WIN || match.state == MatchState.DRAW

    val player1Symbol = "X"
    val player2Symbol = "O"

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
            webSocketMessage = webSocketMessage,
            isGameOver = isGameOver,
            onForfeitClick = onForfeitClick,
            onPlayAgainClick = onPlayAgainClick
        )
    }
}

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

    Text(status, style = MaterialTheme.typography.h5,  color = CustomColor.LightBrown.value)
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
    webSocketMessage: String?,
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
            webSocketMessage?.let {
                Box(modifier = Modifier.height(elementHeight)){
                    Text(
                        text = "Match Message: $it",
                        color = CustomColor.LightBrown.value,
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