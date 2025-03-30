import domain.Move
import domain.Player
import domain.TicTacToeMove
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlin.test.Test

class SerializerTests {
    @Test
    fun `Test serializer`() {
        val module = SerializersModule {
            polymorphic(Move::class) {
                subclass(TicTacToeMove::class)
            }
        }

        val json = Json {
            serializersModule = module
            classDiscriminator = "type"  // Ensures the type info is stored
            prettyPrint = true
        }
        val move: Move = TicTacToeMove(Player.BLACK, 1, 'A')

        // Serialize the move
        val jsonData = json.encodeToString(Move.serializer(), move)
        println("Serialized JSON:\n$jsonData")

        // Deserialize it back
        val deserializedMove = json.decodeFromString<Move>(jsonData)
        println("Deserialized Object:\n$deserializedMove")
    }
}
