package domain

import httpModel.MoveOutput
import httpModel.TicTacToeMoveOutput

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

fun String.toMove(gameType: String): Move? {
    when(gameType) {
        "tic" -> {
            val values = split(",")
            return TicTacToeMove(values[0].toPlayer() ?: return null, values[1].toSquare(TicTacToeBoard.BOARD_DIM))
        }
        else -> return null
    }
}

fun MoveOutput.toMove(): Move? {
    return when (this) {
        is TicTacToeMoveOutput -> TicTacToeMove(player.toPlayer() ?: return null, square.toSquare(TicTacToeBoard.BOARD_DIM))
    }
}