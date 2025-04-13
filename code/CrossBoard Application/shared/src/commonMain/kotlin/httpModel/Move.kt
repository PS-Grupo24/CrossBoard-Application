package httpModel
import domain.*
import domain.GameType.TicTacToe
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
sealed interface MoveInput

@Serializable
data class TicTacToeMoveInput(
    val player: String,
    val square: String
): MoveInput

/*fun parseMoveInput(body: String, gametype: GameType): MoveInput? {
    return when(gametype) {
        TicTacToe -> Json.decodeFromString<TicTacToeMoveInput>(body)
    }
}*/

fun MoveInput.toMove(gametype: GameType) : Move? {
    when(gametype){
        TicTacToe -> {
            this as TicTacToeMoveInput
            val player = this.player.toPlayer() ?: return null
            val square = this.square.toSquare(TicTacToeBoard.BOARD_DIM)

            return TicTacToeMove(player, square)
        }
        else -> return null
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