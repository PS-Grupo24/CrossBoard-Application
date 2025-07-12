package com.crossBoard.httpModel.moveInput

import kotlinx.serialization.Serializable

@Serializable
data class ReversiMoveInput(
    val player: String,
    val square: String
): MoveInput
