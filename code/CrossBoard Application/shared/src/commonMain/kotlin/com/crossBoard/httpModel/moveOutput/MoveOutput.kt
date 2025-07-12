package com.crossBoard.httpModel.moveOutput

import com.crossBoard.domain.MatchType
import com.crossBoard.domain.matchModule.ModuleProvider
import com.crossBoard.domain.move.Move
import kotlinx.serialization.Serializable

/**
 * General MoveOutput contract for the HTTP responses.
 */
@Serializable
sealed interface MoveOutput

@Suppress("UNCHECKED_CAST")
fun Move.toMoveOutput(matchType: MatchType) : MoveOutput {
    val module = ModuleProvider.getModule(matchType)
    return module.moveToMoveOutput(this)
}