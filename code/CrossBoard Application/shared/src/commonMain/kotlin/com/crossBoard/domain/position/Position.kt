package com.crossBoard.domain.position

import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Square
import com.crossBoard.domain.toPlayer
import com.crossBoard.domain.toSquare

/**
 * Interface "Position" represents a position of the square in the game.
 * @property square the square of the position.
 */
interface Position{
    val square: Square
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
        MatchType.Reversi -> ReversiPosition(values[0].toPlayer(), values[1].toSquare(boardDim))
    }
}