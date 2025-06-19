package com.crossBoard

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

/**
 * The JSON implementation responsible for the server serialization
 */
val json = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
}

/**
 * Responsible for configuring the serialization in the server.
 */
fun Application.configureSerialization() {

    install(ContentNegotiation) {
        json(json)
    }
}