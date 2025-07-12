package com.crossBoard.domain.position

import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Square
import com.crossBoard.domain.matchModule.ModuleProvider

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
fun String.toPosition(matchType: MatchType): Position {
    val module = ModuleProvider.getModule(matchType)
    return module.stringToPosition(this)
}

fun positionToString(position: Position, matchType: MatchType): String {
    val module = ModuleProvider.getModule(matchType)
    return module.positionToString(position)
}