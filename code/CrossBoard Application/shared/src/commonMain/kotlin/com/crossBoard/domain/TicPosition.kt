package com.crossBoard.domain

/**
 * Interface "Position" represents a position of the square in the game.
 * @property square the square of the position.
 */
interface Position{
    val square: Square
}

/**
 * Data class "TicPosition" represents a position in the game of TicTacToe.
 * @param player the player at the position.
 * @param square the square of the position.
 * @return Position the position of the said square.
 */
data class TicPosition(val player: Player, override val square: Square) : Position {

    /**
     * Function "toString" responsible to convert the TicPosition object to a String.
     * @return String the String representation of the TicPosition object.
     */
    override fun toString(): String = "${player},${square}"
}

/**
 * Function "toPosition" responsible to convert a String to a Position.
 * @param boardDim the dimension of the board.
 * @param matchType the type of the match.
 * @return Position the Position corresponding to the String and the type of the match.
 */
fun String.toPosition(boardDim: Int, matchType: MatchType): Position {
    val values = split(",")
    return when(matchType){
        MatchType.TicTacToe ->  TicPosition(values[0].toPlayer(), values[1].toSquare(boardDim))
    }
}