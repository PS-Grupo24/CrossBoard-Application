package org.example.project

import io.ktor.client.*
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

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
        install(DefaultRequest) {
            url {
                host = "http://127.0.0.1:8080"
                contentType(ContentType.Application.Json)
            }
        }
    }
}