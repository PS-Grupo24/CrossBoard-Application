package com.crossBoard.utils

import io.ktor.client.*
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*

fun createHttpClient(engine: HttpClientEngine): HttpClient {

    return HttpClient(engine) {
        install(ContentNegotiation) {
            json(
                json = Json{
                    prettyPrint = true
                    ignoreUnknownKeys = true
                }
            )
        }

        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    println("KTOR HTTP LOG: $message")
                }
            }
        }
    }
}