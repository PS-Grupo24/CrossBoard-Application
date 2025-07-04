package com.crossBoard.domain.position

import com.crossBoard.domain.Player
import com.crossBoard.domain.Square

/**
 * Class "ReversiPosition" represents a position in the Reversi game.
 * @param player the player who occupies the position.
 * @param square the square on the board where the position is located.
 * @return Position the Reversi position.
 */
data class ReversiPosition(val player: Player, override val square: Square): Position {
    override fun toString(): String = "${player},${square}"
}