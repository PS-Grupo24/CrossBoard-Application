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
    val gameType: String,
    val version: Int
)

@Serializable
data class MatchPlayedOutput(val move: MoveOutput, val version: Int)

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
    val moves: List<String>,
    val state: String
)

fun BoardOutput.toBoard(gameType: String, player1Type: String): Board? {
    val type = gameType.toGameType()
    when(type) {
        GameType.TicTacToe -> {
            val pos = positions.map {
                it.toPosition(TicTacToeBoard.BOARD_DIM, type) ?: return null
            }
            val mov = moves.map { it.toMove(gameType) ?: return null }
            val tur = turn.toPlayer() ?: return null
            val player1 = player1Type.toPlayer() ?: return null
            val player2 = player1.other()
            return when(state){
                RUNNING_STATE -> TicTacToeBoardRun(
                    pos,
                    mov,
                    tur,
                    player1,
                    player2
                )
                WIN_STATE -> TicTacToeBoardWin(
                    winner?.toPlayer() ?: return null,
                    pos,
                    mov,
                    tur,
                    player1,
                    player2
                    )
                DRAW_STATE -> TicTacToeBoardDraw(
                    pos,
                    mov,
                    tur,
                    player1,
                    player2
                )
                else -> null
            }

        }
        else -> {
            return null
        }
    }
}


fun MatchOutput.toMultiplayerMatch() : MultiPlayerMatch? {
    return MultiPlayerMatch(
        board.toBoard(gameType, player1.playerType) ?: return null,
        matchId,
        player1.userId ?: return null,
        player2.userId,
        gameType.toGameType() ?: return null,
        version
    )
}