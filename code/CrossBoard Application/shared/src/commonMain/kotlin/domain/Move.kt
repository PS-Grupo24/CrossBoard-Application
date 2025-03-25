package domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Move {
    val player: Player
}

/**
 * Data class "Move" represents a move in the game.
 * @param player the player who made the move.
 * @param square the square where the move was made.
 */
@Serializable
@SerialName("TicTacToeMove")
data class TicTacToeMove (override val player: Player, val row: Int, val column: Char) : Move

