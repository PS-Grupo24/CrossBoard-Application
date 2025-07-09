package com.crossBoard.domain.move

import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.board.Board
import com.crossBoard.domain.board.ReversiBoard
import com.crossBoard.domain.board.TicTacToeBoard
import com.crossBoard.domain.matchModule.MatchModule
import com.crossBoard.domain.matchModule.modules
import com.crossBoard.domain.position.Position
import com.crossBoard.domain.toPlayer
import com.crossBoard.domain.toSquare
import com.crossBoard.httpModel.MoveInput
import com.crossBoard.httpModel.MoveOutput
import com.crossBoard.httpModel.ReversiMoveOutput
import com.crossBoard.httpModel.TicTacToeMoveOutput

/**
 * Interface "Move" represents a move in the game.
 * @property player the player who made the move.
 */
sealed interface Move {
    val player: Player
}

/**
 * Function "moveToString" responsible to pass move information to String.
 * @param move the move to be converted to String.
 * @return String the String representation of the move.
 */
@Suppress("UNCHECKED_CAST")
fun moveToString(move: Move, matchType: MatchType): String {
    val module = modules.find { it.matchType == matchType } ?:
    throw IllegalArgumentException("No module found for $matchType")

    return (module as MatchModule<Board, Move, Position, MoveInput, MoveOutput>).moveToString(move)
}

/**
 * Function "toMove" responsible to convert a String to a Move.
 * @param matchType the type of the match.
 * @return Move the Move corresponding to the String and the type of the match.
 */
@Suppress("UNCHECKED_CAST")
fun String.toMove(matchType: MatchType): Move {
    val module = modules.find { it.matchType == matchType }
        ?: throw IllegalArgumentException("No module found for $matchType")

    return (module as MatchModule<Board, Move, Position, MoveInput, MoveOutput>).stringToMove(this)

}

/**
 * Function "toMove" responsible to convert a MoveOutput to a Move.
 * @return Move the Move corresponding to the MoveOutput.
 */
@Suppress("UNCHECKED_CAST")
fun MoveOutput.toMove(matchType: MatchType): Move {
    val module = modules.find { it.matchType == matchType } ?:
        throw IllegalArgumentException("Unknown move type for Math type : $matchType")

    return (module as MatchModule<Board, Move, Position, MoveInput, MoveOutput>).moveOutputToMove(this)
}