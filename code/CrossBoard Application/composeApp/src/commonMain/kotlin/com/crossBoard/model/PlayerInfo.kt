package com.crossBoard.model

/**
 * PlayerInfo responsible for saving each player's information to be displayed.
 * @param id The id of the player.
 * @param username The username of the player.
 */
data class PlayerInfo(
    val id: Int?,
    val username: String,

    ){
    override fun toString(): String {
        return username
    }
}