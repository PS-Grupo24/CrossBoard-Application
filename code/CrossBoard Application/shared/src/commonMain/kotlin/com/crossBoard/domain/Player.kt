package com.crossBoard.domain

/**
 * Enum class "Player" represents the Player in the application of the game.
 * TODO("Remove Empty to use null instead")
 */
enum class Player {
    WHITE,
    BLACK,
    EMPTY;

    /**
     * Function "other" used to get the opposite type of player.
     * @return Player, the opposite player or empty.
     */
    fun other() = when(this) {
        EMPTY -> EMPTY
        BLACK -> WHITE
        WHITE -> BLACK
    }

    companion object{
        /**
         * Function random used to get a random type of player.
         */
        fun random() = listOf(BLACK, WHITE).random()
    }

    /**
     * Function "toString" used to get the String representation of the player.
     * @return String, the String representation of the player.
     */
    override fun toString(): String = when(this) {
            WHITE -> "WHITE"
            BLACK -> "BLACK"
            EMPTY -> "EMPTY"
    }
}

/**
 * Function to convert a String to a Player.
 * @return Player the Player corresponding to the String.
 */
fun String.toPlayer() : Player = when(this) {
        "BLACK" -> Player.BLACK
        "WHITE" -> Player.WHITE
        "EMPTY" -> Player.EMPTY
        else -> throw IllegalArgumentException("Unknown player $this")
}