package com.crossBoard.httpModel.moveOutput

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class TicTacToeMoveOutput represents the format of the expected `MoveOutput` in a `TicTacToe` match.
 * @param player The player type making the move.
 * @param square The target square to be made a move on.
 */
@Serializable
@SerialName("ticMoveOutput")
data class TicTacToeMoveOutput(
    val player: String,
    val square: String
) : MoveOutput
