package com.crossBoard.domain.move

import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.board.TicTacToeBoard
import com.crossBoard.domain.toPlayer
import com.crossBoard.domain.toSquare
import com.crossBoard.httpModel.MoveOutput
import com.crossBoard.httpModel.TicTacToeMoveOutput

/**
 * Interface "Move" represents a move in the game.
 * @property player the player who made the move.
 */
sealed interface Move {
    val player: Player
}

/**
 * Function "moveToString" responsible to pass a move information to String.
 * @param move the move to be converted to String.
 * @return String the String representation of the move.
 */
fun moveToString(move: Move): String = when (move) {
        is TicTacToeMove -> {
            "${move.player},${move.square}"
        }
    }

/**
 * Function "toMove" responsible to convert a String to a Move.
 * @param matchType the type of the match.
 * @return Move the Move corresponding to the String and the type of the match.
 */
fun String.toMove(matchType: MatchType): Move = when(matchType) {
        MatchType.TicTacToe -> {
            val values = split(",")
            TicTacToeMove(values[0].toPlayer(), values[1].toSquare(TicTacToeBoard.BOARD_DIM))
        }
    }

/**
 * Function "toMove" responsible to convert a MoveOutput to a Move.
 * @return Move the Move corresponding to the MoveOutput.
 */
fun MoveOutput.toMove(): Move = when (this) {
        is TicTacToeMoveOutput -> TicTacToeMove(player.toPlayer(), square.toSquare(TicTacToeBoard.BOARD_DIM))
    }