package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.*
import httpModel.MatchOutput

@Composable
fun GameScreen(
    match: MultiPlayerMatch,
    currentUserId: Int?,
    onCellClick: (row: Int, col: Int) -> Unit,
    onForfeitClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onPlayAgainClick: () -> Unit
){
    val board = match.board
    val isGameOver = match.board !is BoardRun

    val p1Symbol = "X"
    val p2Symbol = "O"
    val turnSymbol = if (board.turn == match.getPlayerType(match.player1)) p1Symbol else p2Symbol
    val status = when(board){

        is BoardRun -> "Turn: $turnSymbol"
        is BoardWin -> "Winner: ${board.winner}"
        is BoardDraw -> "Draw"
        else -> "Unknown"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text("Match ID: ${match.id}", style = MaterialTheme.typography.h2)
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Text("P1 ($p1Symbol): ${match.player1}")
            Text("P2 ($p2Symbol): ${match.player2?: "Waiting"}")
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