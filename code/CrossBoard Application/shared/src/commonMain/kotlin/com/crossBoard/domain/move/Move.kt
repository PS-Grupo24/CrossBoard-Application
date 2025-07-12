package com.crossBoard.domain.move

import com.crossBoard.domain.Player

/**
 * Interface "Move" represents a move in the game.
 * @property player the player who made the move.
 */
sealed interface Move {
    val player: Player
}