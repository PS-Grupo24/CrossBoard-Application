package httpModel
import domain.*
import domain.GameType.TicTacToe
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

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

fun MoveInput.toMove() : Move? {
    when(this){
        is TicTacToeMoveInput -> {
            val player = this.player.toPlayer() ?: return null
            val square = this.square.toSquare(TicTacToeBoard.BOARD_DIM)

            return TicTacToeMove(player, square)
        }
        else -> return null
    }
}


fun getMoveInputClass(gameType: GameType): KClass<TicTacToeMoveInput> {
    return when(gameType){
        TicTacToe -> TicTacToeMoveInput::class
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