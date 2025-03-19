package domain

import kotlinx.serialization.Serializable

interface Move {
    val player: Player
}

/**
 * Data class "Move" represents a move in the game.
 * @param player the player who made the move.
 * @param square the square where the move was made.
 */
@Serializable
data class TicTacToeMove (override val player: Player, val square: Square) : Move

