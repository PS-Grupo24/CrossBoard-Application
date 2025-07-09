package com.crossBoard.domain.move

import com.crossBoard.domain.Player
import com.crossBoard.domain.Square

/**
 * Data class "TicTacToeMove" represents a move in the game TicTacToe.
 * @param player the player who made the move.
 * @param square the square where the move was made.
 * @return [Move] The general move.
 */
data class TicTacToeMove(override val player: Player, val square: Square): Move{
    override fun toString(): String = "$player,$square"
}