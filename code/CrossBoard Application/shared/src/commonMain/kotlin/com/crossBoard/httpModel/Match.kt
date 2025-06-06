package com.crossBoard.httpModel

import com.crossBoard.domain.MultiPlayerMatch
import com.crossBoard.domain.toMatchState
import com.crossBoard.domain.toMatchType
import kotlinx.serialization.Serializable

@Serializable
data class MatchOutput(
    val matchId: Int,
    val player1: PlayerOutput,
    val player2: PlayerOutput,
    val board: BoardOutput,
    val gameType: String,
    val version: Int,
    val state: String,
    val winner: Int? = null,
)

fun MatchOutput.toMultiplayerMatch() : MultiPlayerMatch? {
    return MultiPlayerMatch(
        board.toBoard(gameType, player1.playerType, state),
        matchId,
        state.toMatchState(),
        player1.userId ?: return null,
        player2.userId,
        gameType.toMatchType(),
        version,
        winner
    )
}

@Serializable
data class MatchPlayedOutput(val move: MoveOutput, val version: Int)

@Serializable
data class MatchCancel(
    val playerId:Int,
    val matchId: Int
)

