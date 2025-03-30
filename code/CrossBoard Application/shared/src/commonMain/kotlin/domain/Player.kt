package domain


/**
 * enum class "Player" represents the Player in the application of the game.
 * @property char represents the char used to identify the type of player.
 */
enum class Player(val char: Char) {
    WHITE('w'),
    BLACK('b'),
    EMPTY('e');

    /**
     * Function "other" used to obtain the opposite type of player.
     * @return Player, the opposite player or empty.
     */
    fun other() = when(this){
        EMPTY -> EMPTY
        BLACK -> WHITE
        WHITE -> BLACK
    }

    companion object{
        /**
         * Function random used to obtain a random type of player.
         */
        fun random() = listOf(BLACK, WHITE).random()
    }

    override fun toString(): String {
        return when(this){
            WHITE -> "WHITE"
            BLACK -> "BLACK"
            EMPTY -> "EMPTY"
        }
    }
}

fun String.toPlayer() : Player? {
    return when(this){
        "BLACK" -> Player.BLACK
        "WHITE" -> Player.WHITE
        else -> null
    }
}