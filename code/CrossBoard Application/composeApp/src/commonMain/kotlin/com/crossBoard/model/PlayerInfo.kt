package com.crossBoard.model

/**
 * PlayerInfo responsible for saving each player's information to be displayed.
 * @param id The id of the player.
 * @param username The username of the player.
 * @param symbol The symbol to be displayed to reference this player.
 */
data class PlayerInfo(
    val id: Int?,
    val username: String,
    val symbol: String,

    ){
    override fun toString(): String {
        return "$username->($symbol)"
    }
}