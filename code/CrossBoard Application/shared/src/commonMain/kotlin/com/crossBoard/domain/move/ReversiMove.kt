package com.crossBoard.domain.move

import com.crossBoard.domain.*
import com.crossBoard.domain.position.ReversiPosition

/**
 * Class `ReversiMove` represents a move in the game of Reversi.
 * @param player the player who made the move.
 * @param square the square where the move was made.
 */
data class ReversiMove(override val player: Player, val square: Square): Move{
    override fun toString(): String = "$player,$square"
}


