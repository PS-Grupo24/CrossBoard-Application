//import httpModel.MoveInput
import httpModel.MoveInput
import httpModel.TicTacToeMoveInput
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass


fun Application.configureSerialization(){
    val appJsonModule = SerializersModule {
        polymorphic(MoveInput::class) {
            subclass(TicTacToeMoveInput::class)
        }
    }
    install(ContentNegotiation){
        json(Json{
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true

            classDiscriminator = "type"
            serializersModule = appJsonModule
        })
    }
}