package com.crossBoard.httpModel

import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.board.*
import com.crossBoard.domain.matchModule.MatchModule
import com.crossBoard.domain.matchModule.modules
import com.crossBoard.domain.move.Move
import com.crossBoard.domain.move.moveToString
import com.crossBoard.domain.position.Position
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
    val moves: List<String>,
)

/**
 * Function `toBoardOutput` converts a Board into its output format for an http response.
 * @param winner The winner.
 */
fun Board.toBoardOutput(winner: Player?, matchType: MatchType): BoardOutput = BoardOutput(
    player1.toString(),
    player2.toString(),
    winner?.toString(),
    turn.toString(),
    positions.map { it.toString() },
    moves.map { moveToString(it, matchType = matchType ) },
)

/**
 * Function "toBoard" responsible to convert a BoardOutput object to a Board object.
 * @param matchType the type of the match played.
 * @param state the state of the match.
 * @return `Board` object corresponding to the `BoardOutput` object.
 */
@Suppress("UNCHECKED_CAST")
fun BoardOutput.toBoard(matchType: String, state: String): Board {
    val module = modules.find{ it.matchType == MatchType.valueOf(matchType) }
        ?: throw IllegalArgumentException("Module for match type : $matchType not found")

    return (module as MatchModule<Board, Move, Position, MoveInput, MoveOutput>).boardOutputToBoard(this, state)
}