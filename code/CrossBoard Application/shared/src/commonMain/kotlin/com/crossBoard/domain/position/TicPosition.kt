package com.crossBoard.domain.position

import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.Square
import com.crossBoard.domain.toPlayer
import com.crossBoard.domain.toSquare



/**
 * Data class "TicPosition" represents a position in the game of TicTacToe.
 * @param player the player at the position.
 * @param square the square of the position.
 * @return Position the position of the said square.
 */
data class TicPosition(val player: Player, override val square: Square) : Position {

    /**
     * Function "toString" responsible to convert the TicPosition object to a String.
     * @return String the String representation of the TicPosition object.
     */
    override fun toString(): String = "${player},${square}"
}
