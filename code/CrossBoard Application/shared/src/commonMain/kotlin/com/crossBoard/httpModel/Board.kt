package com.crossBoard.httpModel

import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.board.*
import com.crossBoard.domain.matchModule.ModuleProvider
import com.crossBoard.domain.position.positionToString
import com.crossBoard.domain.toPlayer
import com.crossBoard.httpModel.moveOutput.MoveOutput
import kotlinx.serialization.Serializable

/**
 * Data class "BoardOutput" represents the output of a board came from the http response.
 * @param winner the winner of the board.
 * @param turn the player who has the turn to play.
 * @param positions the list of positions in the board.
 * @param moves the list of moves made in the game.
 */
@Serializable
data class BoardOutput(
    val player1: String,
    val player2: String,
    val winner: String?,
    val turn: String,
    val positions: List<String>,
    val moves: List<MoveOutput>,
)

/**
 * Function `toBoardOutput` converts a Board into its output format for an http response.
 * @param winner The winner.
 */
fun Board.toBoardOutput(winner: Player?, matchType: MatchType): BoardOutput {
    val module = ModuleProvider.getModule(matchType)
    return BoardOutput(
        player1.toString(),
        player2.toString(),
        winner?.toString(),
        turn.toString(),
        positions.map { positionToString(it, matchType) },
        moves.map { module.moveToMoveOutput(it) },
    )
}

/**
 * Function "toBoard" responsible to convert a BoardOutput object to a Board object.
 * @param matchType the type of the match played.
 * @param state the state of the match.
 * @return `Board` object corresponding to the `BoardOutput` object.
 */
@Suppress("UNCHECKED_CAST")
fun BoardOutput.toBoard(matchType: String, state: String): Board {
    val module = ModuleProvider.getModule(MatchType.valueOf(matchType))
    val turn = turn.toPlayer()
    val player1 = player1.toPlayer()
    val player2 = player1.other()
    val winner = winner?.toPlayer()
    val moves = this.moves.map { move -> module.moveOutputToMove(move) }
    val positions = this.positions.map { position -> module.stringToPosition(position) }
    return module.getBoard(positions, moves, player1, player2, turn, winner, MatchState.valueOf(state))
}