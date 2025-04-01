package httpModel

import domain.*
import kotlinx.serialization.Serializable


@Serializable
data class MatchCreationInput(
    val gameType: String
)

@Serializable
data class MatchOutput(
    val matchId: Int,
    val player1: PlayerOutput,
    val player2: PlayerOutput,
    val board: BoardOutput,
    val gameType: String
)

@Serializable
data class PlayerOutput(
    val userId: Int?,
    val playerType: String
)

@Serializable
data class BoardOutput(
    val winner: String?,
    val turn: String,
    val positions: List<String>,
    val state: String
)

fun MultiPlayerMatch.toMatchOutput() : MatchOutput {
    val winner = if (board is BoardWin) board.winner.toString() else null
    return MatchOutput(
        id,
        PlayerOutput(
            player1,
            getPlayerType(player1).toString()
        ),
        PlayerOutput(
            player2,
            getPlayerType(player1).other().toString()
        ),
        BoardOutput(
            winner,
            board.turn.toString(),
            board.positions.map { it.toString() },
            getBoardState(board)
            ),
        gameType.toString()
    )
}