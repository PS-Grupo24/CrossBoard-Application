package com.crossBoard.httpModel.moveHttp

import kotlinx.serialization.Serializable

@Serializable
data class ReversiMoveHttp(
    val player: String,
    val square: String
): MoveHttp