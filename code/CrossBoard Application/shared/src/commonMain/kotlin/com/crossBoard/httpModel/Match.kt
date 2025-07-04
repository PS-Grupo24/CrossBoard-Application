package com.crossBoard.httpModel

import com.crossBoard.domain.MultiPlayerMatch
import com.crossBoard.domain.toMatchState
import com.crossBoard.domain.toMatchType
import kotlinx.serialization.Serializable

/**
 * Format used for the `Match` in an HTTP response.
 * @param matchId The id of the match.
 * @param user1Info The information of the user1 in the match.
 * @param user2Info The information of the user2 in the match.
 * @param board The information of the board in this match.
 * @param matchType The type of game in this match.
 * @param version The current match version.
 * @param state The current state of this match.
 * @param winner The id of the winner for this match or `NULL` if match is not won yet.
 */
@Serializable
data class MatchOutput(
    val matchId: Int,
    val user1Info: PlayerOutput,
    val user2Info: PlayerOutput,
    val board: BoardOutput,
    val matchType: String,
    val version: Int,
    val state: String,
    val winner: Int? = null,
)

/**
 * Auxiliary function to convert the data in a `MatchOutput` format in an HTTP response into an actual match.
 * @return `MultiPlayerMatch` if the convertion is successful; `NULL` if there was an error in convertion.
 */
fun MatchOutput.toMultiplayerMatch() : MultiPlayerMatch {
    return MultiPlayerMatch(
        board.toBoard(matchType, user1Info.playerType, state),
        matchId,
        state.toMatchState(),
        user1Info.userId ?: throw IllegalStateException("User1 ID must not be null"),
        user2Info.userId,
        matchType.toMatchType(),
        version,
        winner
    )
}

/**
 * Data class MatchPlayedOutput represents information to be sent in an HTTP response when a move is made in a match.
 * @param move The `MoveOutput` representing the information of the move made in the match.
 * @param version The new version of the match after the move was made.
 */
@Serializable
data class MatchPlayedOutput(val move: MoveOutput, val version: Int)

/**
 * Data class MatchCancel represents the information to be sent in an HTTP response after a `Match` is canceled.
 * @param userId The id of the user that performed the match cancellation.
 * @param matchId The id of the match that was canceled.
 */
@Serializable
data class MatchCancel(
    val userId:Int,
    val matchId: Int
)

