package org.example.project

import httpModel.MoveInput
import httpModel.TicTacToeMoveInput
import io.ktor.client.*
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.modules.*

val appJsonModule = SerializersModule {
    polymorphic(MoveInput::class){
        subclass(TicTacToeMoveInput::class)
    }
}
val clientJson = Json{
    prettyPrint = true
    ignoreUnknownKeys = true
    serializersModule = appJsonModule
    classDiscriminator = "type"
}

fun createHttpClient(engine: HttpClientEngine): HttpClient {

    return HttpClient(engine) {
        install(ContentNegotiation) {
            json(
                json = clientJson
            )
        }

        install(Logging) {
            println("--- Ktor Logging Plugin Installed ---") // Add this temporary check
            level = LogLevel.ALL
            logger = object : Logger { // Using the explicit logger from above
                override fun log(message: String) {
                    println("KTOR HTTP LOG: $message")
                }
            }
        }
    }
}