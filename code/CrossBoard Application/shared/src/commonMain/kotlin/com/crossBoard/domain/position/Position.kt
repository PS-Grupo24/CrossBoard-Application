package com.crossBoard.domain.position

import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Square
import com.crossBoard.domain.board.Board
import com.crossBoard.domain.matchModule.MatchModule
import com.crossBoard.domain.matchModule.modules
import com.crossBoard.domain.move.Move
import com.crossBoard.domain.toPlayer
import com.crossBoard.domain.toSquare
import com.crossBoard.httpModel.MoveInput
import com.crossBoard.httpModel.MoveOutput

/**
 * Interface "Position" represents a position of the square in the game.
 * @property square the square of the position.
 */
interface Position{
    val square: Square
}

/**
 * Function "toPosition" responsible to convert a String to a Position.
 * @param matchType the type of the match.
 * @return Position the Position corresponding to the String and the type of the match.
 * @throws IllegalArgumentException When the received this contains more than 2 ','.
 */
@Suppress("UNCHECKED_CAST")
fun String.toPosition(matchType: MatchType): Position {
    val module = modules.find{ it.matchType == matchType }
        ?: throw IllegalArgumentException("No module found for match type $matchType")

    return (module as MatchModule<Board, Move, Position, MoveInput, MoveOutput>).stringToPosition(this)
}