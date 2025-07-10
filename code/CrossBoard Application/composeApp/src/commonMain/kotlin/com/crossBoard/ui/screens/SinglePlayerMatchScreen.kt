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
import ticTacToeBoardView
import kotlin.Int.Companion.MAX_VALUE


/**
 * Screen responsible for the display of a single match.
 * @param user The current logged user; `NULL` for an anonymous user.
 * @param match The current single player match.
 * @param player The player type of the user.
 * @param errorMessage The current error message; `NULL` when there is no error message.
 * @param onMakeMove The action to perform when making a move.
 * @param onForfeit The action to perform when the forfeit button is clicked.
 * @param onPlayAgain The action to perform when the play again button is clicked.
 */
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
            PlayerInfo(userId, user?.username?.value ?: "Anonymous"),
            PlayerInfo(0, "Machine"),
            null
        )

        val turnString = if (match.board.turn == player) "Your turn" else "Opponents turn"
        val status = when(match.board){
            is BoardRun -> turnString
            is BoardWin -> {
                val winner = if (match.board.winner == player) "You Won" else "You Lost"
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
                    player,
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
                    }
                )
            }
            MatchType.Reversi -> {
                reversiBoardView(
                    match.board,
                    myPlayerType = player,
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
                    }
                )
            }
        }

        GameActions(
            isLoading = false,
            errorMessage = errorMessage,
            isGameOver = isMatchOver,
            onForfeitClick = onForfeit,
            onPlayAgainClick = onPlayAgain
        )
    }
}