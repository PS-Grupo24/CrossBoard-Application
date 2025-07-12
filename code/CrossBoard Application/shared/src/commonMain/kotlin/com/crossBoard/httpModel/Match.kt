package com.crossBoard.httpModel

import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MultiPlayerMatch
import com.crossBoard.domain.board.BoardWin
import com.crossBoard.domain.toMatchState
import com.crossBoard.domain.toMatchType
import com.crossBoard.httpModel.moveHttp.MoveHttp
import com.crossBoard.httpModel.moveHttp.toMoveHttp
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
    val user1Info: Int,
    val user2Info: Int?,
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
        board.toBoard(matchType, state),
        matchId,
        state.toMatchState(),
        user1Info,
        user2Info,
        matchType.toMatchType(),
        version,
        winner
    )
}

/**
 * Function to convert a MultiPlayerMatch to a MatchOutput.
 * @return `MatchOutput` The match output.
 */
fun MultiPlayerMatch.toMatchOutput(): MatchOutput {
    val winner = if (board is BoardWin) board.winner else null
    val winnerId = when(state){
        MatchState.WIN -> {
            val player1Type = getPlayerType(user1)
            if(winner == player1Type)
                user1
            else
                user2
        }
        else -> null
    }
    return MatchOutput(
        id,
        user1,
        user2,
        board.toBoardOutput(winner, matchType)
        ,
        matchType.toString(),
        version,
        state.toString(),
        winnerId
    )
}

/**
 * Function to convert a MultiPlayerMatch to a MatchPlayedOutput.
 * @return MatchPlayedOutput the match played output.
 */
fun MultiPlayerMatch.toPlayedMatch() = MatchPlayedOutput(this.board.moves.last().toMoveHttp(matchType), this.version)

/**
 * Data class MatchPlayedOutput represents information to be sent in an HTTP response when a move is made in a match.
 * @param move The `MoveOutput` representing the information of the move made in the match.
 * @param version The new version of the match after the move was made.
 */
@Serializable
data class MatchPlayedOutput(val move: MoveHttp, val version: Int)

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

