package domain

import kotlinx.serialization.Serializable

/**
 * Data class "Position" represents a position in the game.
 * @param player the player at the position.
 * @param square the square of the position.
 */
data class Position(val player: Player, val square: Square)