package com.crossBoard.domain.move

import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.matchModule.ModuleProvider
import com.crossBoard.httpModel.moveOutput.MoveOutput

/**
 * Interface "Move" represents a move in the game.
 * @property player the player who made the move.
 */
sealed interface Move {
    val player: Player
}

/**
 * Function "toMove" responsible to convert a MoveOutput to a Move.
 * @return Move the Move corresponding to the MoveOutput.
 */
fun MoveOutput.toMove(matchType: MatchType): Move {
    val module = ModuleProvider.getModule(matchType)
    return module.moveOutputToMove(this)
}