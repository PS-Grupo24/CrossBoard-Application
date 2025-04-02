package domain

/**
 * Data class "Position" represents a position in the game.
 * @param player the player at the position.
 * @param square the square of the position.
 */
data class Position(val player: Player, val square: Square){
    override fun toString(): String {
        return "${player},${square}"
    }
}

fun String.toPosition(boardDim: Int): Position? {
    val values = split(",")
    return Position(values[0].toPlayer() ?: return null, values[1].toSquare(boardDim))
}