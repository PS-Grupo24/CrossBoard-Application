package com.crossBoard.httpModel

import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.board.*
import com.crossBoard.domain.position.TicPosition
import com.crossBoard.domain.move.ReversiMove
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.domain.toMatchState
import com.crossBoard.domain.toMatchType
import com.crossBoard.domain.move.toMove
import com.crossBoard.domain.position.ReversiPosition
import com.crossBoard.domain.toPlayer
import com.crossBoard.domain.position.toPosition
import kotlinx.serialization.Serializable

/**
 * Data class "BoardOutput" represents the output of a board came from the http response.
 * @param winner the winner of the board.
 * @param turn the player who has the turn to play.
 * @param positions the list of positions in the board.
 * @param moves the list of moves made in the game.
 */
@Serializable
data class BoardOutput(
    val winner: String?,
    val turn: String,
    val positions: List<String>,
    val moves: List<String>,
)

/**
 * Function "toBoard" responsible to convert a BoardOutput object to a Board object.
 * @param matchType the type of the match played.
 * @param player1Type the type of the first player.
 * @param state the state of the match.
 * @return `Board` object corresponding to the `BoardOutput` object.
 */
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
        MatchType.Reversi -> {
            val pos = positions.map{
                it.toPosition(ReversiBoard.BOARD_DIM, type) as ReversiPosition
            }
            val mov = moves.map { it.toMove(type) as ReversiMove }

            return when(state.toMatchState()) {
                MatchState.RUNNING -> {
                    ReversiBoardRun(
                        pos,
                        mov,
                        tur,
                        player1,
                        player2
                    )
                }
                MatchState.WAITING -> {
                    ReversiBoardRun(
                        pos,
                        mov,
                        tur,
                        player1,
                        player2
                    )
                }
                MatchState.WIN -> ReversiBoardWin(
                    winner?.toPlayer() ?: throw IllegalArgumentException("Winner must not be null"),
                    pos,
                    mov,
                    tur,
                    player1,
                    player2,
                )
                MatchState.DRAW -> ReversiBoardDraw(
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