package com.crossBoard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.crossBoard.domain.*
import com.crossBoard.domain.board.*
import com.crossBoard.domain.move.Move
import com.crossBoard.domain.move.ReversiMove
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.model.PlayerInfo
import com.crossBoard.model.SinglePlayerMatch
import com.crossBoard.utils.CustomColor
import kotlin.Int.Companion.MAX_VALUE

@Composable
fun SinglePlayerMatchScreen(
    user: User?,
    match: SinglePlayerMatch,
    player: Player,
    errorMessage: String?,
    onMakeMove: (move: Move) -> Unit,
    onForfeit: () -> Unit,
    onPlayAgain: () -> Unit,
    onGoBack: () -> Unit,
){
    val isMatchOver = match.state == MatchState.WIN || match.state == MatchState.DRAW
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ){
        val userId = user?.id ?: MAX_VALUE
        MatchInfoPanel(
            matchId = null,
            currentUserId = userId,
            PlayerInfo(userId, user?.username?.value ?: "Anonymous", player1Symbol),
            PlayerInfo(0, "Machine", player2Symbol),
            null
        )

        val turnSymbol = if (match.board.turn == player) player1Symbol else player2Symbol
        val status = when(match.board){
            is BoardRun -> "Turn: $turnSymbol"
            is BoardWin -> {
                val winner = if (match.board.winner == player) player1Symbol else player2Symbol
                "Winner: $winner"
            }
            is BoardDraw -> "Draw"
            else -> "Unknown State"
        }

        Text(status, style = MaterialTheme.typography.h5,  color = CustomColor.LightBrown.value)
        Spacer(Modifier.height(16.dp))

        when(match.matchType) {
            MatchType.TicTacToe -> {
                ticTacToeBoardView(
                    match.board,
                    onCellClick = { row, col ->
                        onMakeMove(
                            TicTacToeMove(
                                player,
                                Square(
                                    Row(row, TicTacToeBoard.BOARD_DIM),
                                    Column('a' + col)
                                )
                            )
                        )
                    },
                    player1Symbol = player1Symbol,
                    player2Symbol = player2Symbol,
                    player1Type = player
                )
            }
            MatchType.Reversi -> {
                reversiBoardView(
                    match.board,
                    onClick = { row, col ->
                        onMakeMove(
                            ReversiMove(
                                player,
                                Square(
                                    Row(row, ReversiBoard.BOARD_DIM),
                                    Column('a' + col)
                                )
                            )
                        )
                    },
                    player1Symbol = player1Symbol,
                    player2Symbol = player2Symbol,
                    player1Type = player
                )
            }
        }

        GameActions(
            isLoading = false,
            errorMessage = errorMessage,
            webSocketMessage = null,
            isGameOver = isMatchOver,
            onForfeitClick = onForfeit,
            onPlayAgainClick = onPlayAgain
        )
    }
}