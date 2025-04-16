package httpModel
import domain.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface MoveInput

@Serializable
data class TicTacToeMoveInput(
    val player: String,
    val square: String
): MoveInput

fun MoveInput.toMove() : Move {
    when(this){
        is TicTacToeMoveInput -> {
            val player = player.toPlayer()
            val square = square.toSquare(TicTacToeBoard.BOARD_DIM)

            return TicTacToeMove(player, square)
        }
    }
}

@Serializable
sealed interface MoveOutput

@Serializable
@SerialName("ticMoveOutput")
data class TicTacToeMoveOutput(
    val player: String,
    val square: String
) : MoveOutput

fun Move.toMoveOutput() : MoveOutput {
    return when(this){
        is TicTacToeMove -> TicTacToeMoveOutput(player.toString(), square.toString())
    }
}