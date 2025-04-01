package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.DRAW_STATE
import domain.RUNNING_STATE
import domain.WIN_STATE
import httpModel.MatchOutput

@Composable
fun GameScreen(
    match: MatchOutput,
    currentUserId: Int?,
    onCellClick: (row: Int, col: Int) -> Unit,
    onForfeitClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onPlayAgainClick: () -> Unit
){
    val board = match.board
    val isGameOver = board.state != RUNNING_STATE

    val p1Symbol = "X"
    val p2Symbol = "O"

    val status = when(board.state){
        RUNNING_STATE -> "Turn: ${board.turn}"
        WIN_STATE -> "Winner: ${board.winner}"
        DRAW_STATE -> "Draw"
        else -> "Unknown"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text("Match ID: ${match.matchId}", style = MaterialTheme.typography.h2)
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Text("P1 ($p1Symbol): ${match.player1.userId}")
            Text("P2 ($p2Symbol): ${match.player2.userId ?: "Waiting"}")
        }
        Spacer(Modifier.height(16.dp))

        Text(status, style = MaterialTheme.typography.caption)

        Spacer(Modifier.height(16.dp))

        ticTacToeBoardView(
            board = board,
            onCellClick = { row, col ->
                if (!isGameOver) {
                    onCellClick(row, col)
                }
            },
            enabled = !isLoading && !isGameOver
        )
        Spacer(Modifier.height(16.dp))

        if (isLoading){
            CircularProgressIndicator()
            Spacer(Modifier.height(8.dp))
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colors.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
                )
            Spacer(Modifier.height(8.dp))
        }

        if(isGameOver){
            Text("Game Over", style = MaterialTheme.typography.h3)
            Spacer(Modifier.height(8.dp))
            Button(onClick = { onPlayAgainClick() }) {
                Text("Play Again")
            }
        }
        else {
            Button(
                onClick = { onForfeitClick() },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
            ) {
                Text("Forfeit Match")
            }
        }
    }
}