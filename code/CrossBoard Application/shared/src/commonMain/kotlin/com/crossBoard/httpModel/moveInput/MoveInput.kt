package com.crossBoard.httpModel.moveInput

import com.crossBoard.domain.MatchType
import com.crossBoard.domain.matchModule.ModuleProvider
import com.crossBoard.domain.move.Move
import kotlinx.serialization.Serializable

/**
 * General MoveInput contract for the HTTP requests.
 */
@Serializable
sealed interface MoveInput

/**
 * Auxiliary Function that converts a `MoveInput` into an actual `Move` format.
 */
fun MoveInput.toMove(matchType: MatchType) : Move {
    val module = ModuleProvider.getModule(matchType)
    return module.moveInputToMove(this)
}