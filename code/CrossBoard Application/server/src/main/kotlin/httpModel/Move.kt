package httpModel
import domain.Move
import domain.TicTacToeBoard
import domain.TicTacToeMove
import domain.toPlayer
import domain.toSquare
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
sealed interface MoveInput

@Serializable
@SerialName("ticMoveInput")
data class TicTacToeMoveInput(
    val player: String,
    val square: String
) : MoveInput

fun MoveInput.toMove() : Move? {
    when(this){
        is TicTacToeMoveInput -> {
            val player = this.player.toPlayer() ?: return null
            val square = this.square.toSquare(TicTacToeBoard.BOARD_DIM)

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

fun Move.toMoveOutput() : MoveOutput{
    return when(this){
        is TicTacToeMove -> TicTacToeMoveOutput(player.toString(), square.toString())
    }
}