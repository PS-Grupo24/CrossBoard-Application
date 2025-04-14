package domain


interface Position{
    val square: Square
}

/**
 * Data class "Position" represents a position in the game.
 * @param player the player at the position.
 * @param square the square of the position.
 */
data class TicPosition(val player: Player, override val square: Square) : Position{
    override fun toString(): String {
        return "${player},${square}"
    }
}

fun String.toPosition(boardDim: Int, gameType: GameType): TicPosition? {
    val values = split(",")
    return when(gameType){
        GameType.TicTacToe ->  TicPosition(values[0].toPlayer() ?: return null, values[1].toSquare(boardDim))
    }

}