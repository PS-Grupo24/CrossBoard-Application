package com.crossBoard.model

data class PlayerInfo(
    val id: Int?,
    val username: String,
    val symbol: String,

    ){
    override fun toString(): String {
        return "$username->($symbol)"
    }
}