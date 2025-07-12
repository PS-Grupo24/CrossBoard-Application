package com.crossBoard.httpModel.moveHttp

import com.crossBoard.domain.MatchType.TicTacToe
import kotlinx.serialization.Serializable

/**
 * Data class TicTacToeMoveHttp represents the format of the expected [MoveHttp] in a [TicTacToe] match.
 * @param player The player type making the move.
 * @param square The target square to be made a move on.
 */
@Serializable
data class TicTacToeMoveHttp(
    val player: String,
    val square: String
): MoveHttp