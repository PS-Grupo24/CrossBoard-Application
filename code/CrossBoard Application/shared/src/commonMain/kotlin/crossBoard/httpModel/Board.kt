package crossBoard.httpModel

import crossBoard.domain.*
import kotlinx.serialization.Serializable

@Serializable
data class BoardOutput(
    val winner: String?,
    val turn: String,
    val positions: List<String>,
    val moves: List<String>,
)

fun BoardOutput.toBoard(matchType: String, player1Type: String, state: String): Board {

    val tur = turn.toPlayer()
    val player1 = player1Type.toPlayer()
    val player2 = player1.other()

    when(val type = matchType.toMatchType()) {
        MatchType.TicTacToe -> {

            val pos = positions.map {
                it.toPosition(TicTacToeBoard.BOARD_DIM, type) as TicPosition
            }
            val mov = moves.map { it.toMove(type) as TicTacToeMove }

            return when(state.toMatchState()){
                MatchState.RUNNING -> {
                    TicTacToeBoardRun(
                        pos,
                        mov,
                        tur,
                        player1,
                        player2
                    )
                }
                MatchState.WAITING -> {
                    TicTacToeBoardRun(
                        pos,
                        mov,
                        tur,
                        player1,
                        player2
                    )
                }
                MatchState.WIN -> TicTacToeBoardWin(
                    winner?.toPlayer() ?: throw IllegalArgumentException("Winner must not be null"),
                    pos,
                    mov,
                    tur,
                    player1,
                    player2,
                )
                MatchState.DRAW-> TicTacToeBoardDraw(
                    pos,
                    mov,
                    tur,
                    player1,
                    player2,
                )
            }

        }
    }
}