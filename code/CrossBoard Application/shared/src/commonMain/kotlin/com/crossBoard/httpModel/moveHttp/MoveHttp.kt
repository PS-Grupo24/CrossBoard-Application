package com.crossBoard.httpModel.moveHttp

import com.crossBoard.domain.MatchType
import com.crossBoard.domain.matchModule.ModuleProvider
import com.crossBoard.domain.move.Move
import com.crossBoard.domain.Player
import com.crossBoard.domain.Square
import kotlinx.serialization.Serializable

/**
 * Sealed interface [MoveHttp] required to send [Move] in Http Messages.
 * This is required so the Http message only contains parameters with primitive types ([Int], [String], etc...).
 * and not kotlin specific types ([Player], [Square], etc...)
 */
@Serializable
sealed interface MoveHttp


/**
 * Responsible for converting a [Move] into a [MoveHttp] by converting its properties in kotlin types
 * into primitive types.
 *
 * Example:
 *
 * [Player] into [String]
 */
fun Move.toMoveHttp(matchType: MatchType) : MoveHttp{
    val module = ModuleProvider.getModule(matchType)
    return module.moveToMoveHttp(this)
}

/**
 * Responsible for converting a [MoveHttp] into [Move] by converting the primitive type properties
 * into their kotlin types.
 */
fun MoveHttp.toMove(matchType: MatchType) : Move {
    val module = ModuleProvider.getModule(matchType)
    return module.moveHttpToMove(this)
}