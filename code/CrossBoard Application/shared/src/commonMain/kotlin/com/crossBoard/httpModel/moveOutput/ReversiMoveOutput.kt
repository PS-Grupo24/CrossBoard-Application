package com.crossBoard.httpModel.moveOutput

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("reversiMoveOutput")
data class ReversiMoveOutput(
    val player: String,
    val square: String
): MoveOutput