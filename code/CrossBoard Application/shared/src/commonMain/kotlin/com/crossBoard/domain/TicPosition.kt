package com.crossBoard.domain


interface Position{
    val square: Square
}

/**
 * Data class "Position" represents a position in the game.
 * @param player the player at the position.
 * @param square the square of the position.
 */
data class TicPosition(val player: Player, override val square: Square) : Position {
    override fun toString(): String {
        return "${player},${square}"
    }
}

fun String.toPosition(boardDim: Int, matchType: MatchType): Position {
    val values = split(",")
    return when(matchType){
        MatchType.TicTacToe ->  TicPosition(values[0].toPlayer(), values[1].toSquare(boardDim))
    }
}