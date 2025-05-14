package com.crossBoard.domain

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
 * Data class "TicTacToeMove" represents a move in the game TicTacToe.
 * @param player the player who made the move.
 * @param square the square where the move was made.
 * @return Move the type of that was move.
 */
data class TicTacToeMove(override val player: Player, val square: Square): Move {

    /**
     * Function "equals" responsible to compare two TicTacToeMove objects.
     * @param other the other object to compare.
     * @return Boolean true if the objects are equal, false otherwise.
     */
    override fun equals(other: Any?): Boolean =
        other is TicTacToeMove && other.player == player && other.square.row.number == square.row.number && other.square.column.symbol == square.column.symbol

    /**
     * Function "hashCode" responsible to generate a hash code for the TicTacToeMove object.
     * @return Int the hash code of the object.
     */
    override fun hashCode(): Int = player.hashCode() + square.hashCode()
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