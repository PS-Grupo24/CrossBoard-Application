package httpModel

import domain.*
import kotlinx.serialization.Serializable

@Serializable
data class MatchOutput(
    val matchId: Int,
    val player1: PlayerOutput,
    val player2: PlayerOutput,
    val board: BoardOutput,
    val gameType: String,
    val version: Int,
    val state: String
)

fun MatchOutput.toMultiplayerMatch() : MultiPlayerMatch? {
    return MultiPlayerMatch(
        board.toBoard(gameType, player1.playerType, state),
        matchId,
        state.toMatchState(),
        player1.userId ?: return null,
        player2.userId,
        gameType.toMatchType(),
        version
    )
}

@Serializable
data class MatchPlayedOutput(val move: MoveOutput, val version: Int)


