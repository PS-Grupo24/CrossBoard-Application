package com.crossBoard.httpModel

import com.crossBoard.domain.Board
import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.TicPosition
import com.crossBoard.domain.TicTacToeBoard
import com.crossBoard.domain.TicTacToeBoardDraw
import com.crossBoard.domain.TicTacToeBoardRun
import com.crossBoard.domain.TicTacToeBoardWin
import com.crossBoard.domain.TicTacToeMove
import com.crossBoard.domain.toMatchState
import com.crossBoard.domain.toMatchType
import com.crossBoard.domain.toMove
import com.crossBoard.domain.toPlayer
import com.crossBoard.domain.toPosition
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