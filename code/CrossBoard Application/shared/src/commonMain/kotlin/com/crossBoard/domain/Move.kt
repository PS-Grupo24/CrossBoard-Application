package com.crossBoard.domain

import com.crossBoard.httpModel.MoveOutput
import com.crossBoard.httpModel.TicTacToeMoveOutput

sealed interface Move {
    val player: Player
}

/**
 * Data class "Move" represents a move in the game.
 * @param player the player who made the move.
 * @param square the square where the move was made.
 */
data class TicTacToeMove (override val player: Player, val square: Square) : Move


fun moveToString(move: Move): String {
    return when (move) {
        is TicTacToeMove -> {
            "${move.player},${move.square}"
        }
    }
}

fun String.toMove(matchType: MatchType): Move {
    when(matchType) {
        MatchType.TicTacToe -> {
            val values = split(",")
            return TicTacToeMove(values[0].toPlayer(), values[1].toSquare(TicTacToeBoard.BOARD_DIM))
        }
        else -> throw IllegalArgumentException("Invalid type $matchType")
    }
}

fun MoveOutput.toMove(): Move {
    return when (this) {
        is TicTacToeMoveOutput -> TicTacToeMove(player.toPlayer(), square.toSquare(com.crossBoard.domain.TicTacToeBoard.BOARD_DIM))
    }
}