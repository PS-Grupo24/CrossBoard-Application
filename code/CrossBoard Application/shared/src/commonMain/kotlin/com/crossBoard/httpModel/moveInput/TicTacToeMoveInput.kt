package com.crossBoard.httpModel.moveInput

import kotlinx.serialization.Serializable

/**
 * Data class TicTacToeMoveInput represents the format of the expected `MoveInput` in a `TicTacToe` match.
 * @param player The player type making the move.
 * @param square The target square to be made a move on.
 */
@Serializable
data class TicTacToeMoveInput(
    val player: String,
    val square: String
): MoveInput