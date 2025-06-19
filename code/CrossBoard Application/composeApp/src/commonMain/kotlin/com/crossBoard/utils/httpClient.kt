package com.crossBoard.utils

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.sse.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val clientJson = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
}

fun createHttpClient(engine: HttpClientEngine): HttpClient {

    return HttpClient(engine) {
        install(ContentNegotiation) {
            json(
                json = clientJson,
            )
        }

        install(Logging) {
            level = LogLevel.ALL
        }

        install(SSE){

        }
    }
}