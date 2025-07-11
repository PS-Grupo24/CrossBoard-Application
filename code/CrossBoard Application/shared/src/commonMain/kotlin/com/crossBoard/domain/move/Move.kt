package com.crossBoard.domain.move

import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.board.Board
import com.crossBoard.domain.matchModule.MatchModule
import com.crossBoard.domain.matchModule.ModuleProvider
import com.crossBoard.domain.position.Position
import com.crossBoard.httpModel.MoveInput
import com.crossBoard.httpModel.MoveOutput

/**
 * Interface "Move" represents a move in the game.
 * @property player the player who made the move.
 */
sealed interface Move {
    val player: Player
}

/**
 * Function "moveToString" responsible to pass move information to String.
 * @param move the move to be converted to String.
 * @return String the String representation of the move.
 */
@Suppress("UNCHECKED_CAST")
fun moveToString(move: Move, matchType: MatchType): String {
    val module = ModuleProvider.getModule(matchType)
    return module.moveToString(move)
}

/**
 * Function "toMove" responsible to convert a String to a Move.
 * @param matchType the type of the match.
 * @return Move the Move corresponding to the String and the type of the match.
 */
@Suppress("UNCHECKED_CAST")
fun String.toMove(matchType: MatchType): Move {
    val module = ModuleProvider.getModule(matchType)
    return module.stringToMove(this)

}

/**
 * Function "toMove" responsible to convert a MoveOutput to a Move.
 * @return Move the Move corresponding to the MoveOutput.
 */
@Suppress("UNCHECKED_CAST")
fun MoveOutput.toMove(matchType: MatchType): Move {
    val module = ModuleProvider.getModule(matchType)

    return module.moveOutputToMove(this)
}