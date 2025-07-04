package com.crossBoard.utils

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.sse.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * The json configuration for the client.
 */
val clientJson = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
}

/**
 * Auxiliary function that creates an `HTTPClient` given an `HttpClientEngine`.
 * It also performs the configuration of the JSON serialization, Logging and SSE.
 */
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