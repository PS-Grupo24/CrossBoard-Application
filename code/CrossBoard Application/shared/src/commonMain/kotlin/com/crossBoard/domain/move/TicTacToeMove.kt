package com.crossBoard.domain.move

import com.crossBoard.domain.Player
import com.crossBoard.domain.Square

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