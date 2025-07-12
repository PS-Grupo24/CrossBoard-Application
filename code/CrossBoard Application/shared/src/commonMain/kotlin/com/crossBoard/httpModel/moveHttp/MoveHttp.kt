package com.crossBoard.httpModel.moveHttp

import com.crossBoard.domain.MatchType
import com.crossBoard.domain.matchModule.ModuleProvider
import com.crossBoard.domain.move.Move
import kotlinx.serialization.Serializable

@Serializable
sealed interface MoveHttp

fun Move.toMoveHttp(matchType: MatchType) : MoveHttp{
    val module = ModuleProvider.getModule(matchType)
    return module.moveToMoveHttp(this)
}

fun MoveHttp.toMove(matchType: MatchType) : Move {
    val module = ModuleProvider.getModule(matchType)
    return module.moveHttpToMove(this)
}