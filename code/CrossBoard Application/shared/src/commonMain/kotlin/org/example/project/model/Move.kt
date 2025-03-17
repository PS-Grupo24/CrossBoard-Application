package org.example.project.model

/**
 * Data class "Move" represents a move in the game.
 * @param player the player who made the move.
 * @param square the square where the move was made.
 */
data class Move (val player: Player, val square: Square)