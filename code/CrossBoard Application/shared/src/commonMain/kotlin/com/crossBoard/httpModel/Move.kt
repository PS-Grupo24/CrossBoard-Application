package com.crossBoard.httpModel
import com.crossBoard.domain.board.ReversiBoard
import com.crossBoard.domain.move.Move
import com.crossBoard.domain.board.TicTacToeBoard
import com.crossBoard.domain.move.ReversiMove
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.domain.toPlayer
import com.crossBoard.domain.toSquare
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * General MoveInput contract for the HTTP requests.
 */
@Serializable
sealed interface MoveInput

/**
 * Data class TicTacToeMoveInput represents the format of the expected `MoveInput` in a `TicTacToe` match.
 * @param player The player type making the move.
 * @param square The target square to be made a move on.
 */
@Serializable
data class TicTacToeMoveInput(
    val player: String,
    val square: String
): MoveInput

@Serializable
data class ReversiMoveInput(
    val player: String,
    val square: String
): MoveInput

/**
 * Auxiliary Function that converts a `MoveInput` into an actual `Move` format.
 */
fun MoveInput.toMove() : Move {
    when(this){
        is TicTacToeMoveInput -> {
            val player = player.toPlayer()
            val square = square.toSquare(TicTacToeBoard.BOARD_DIM)

            return TicTacToeMove(player, square)
        }
        is ReversiMoveInput -> {
            val player = player.toPlayer()
            val square = square.toSquare(ReversiBoard.BOARD_DIM)

            return ReversiMove(player, square)
        }
    }
}

/**
 * General MoveOutput contract for the HTTP responses.
 */
@Serializable
sealed interface MoveOutput

/**
 * Data class TicTacToeMoveOutput represents the format of the expected `MoveOutput` in a `TicTacToe` match.
 * @param player The player type making the move.
 * @param square The target square to be made a move on.
 */
@Serializable
@SerialName("ticMoveOutput")
data class TicTacToeMoveOutput(
    val player: String,
    val square: String
) : MoveOutput

@Serializable
@SerialName("reversiMoveOutput")
data class ReversiMoveOutput(
    val player: String,
    val square: String
): MoveOutput

/**
 * Auxiliary function that converts a `Move` object into a `MoveOutput` format.
 */
fun Move.toMoveOutput() : MoveOutput {
    return when(this){
        is TicTacToeMove -> TicTacToeMoveOutput(player.toString(), square.toString())
        is ReversiMove -> ReversiMoveOutput(player.toString(), square.toString())
    }
}