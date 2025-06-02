package com.crossBoard

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.OutputFormat
import io.github.smiley4.ktoropenapi.openApi
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureOpenAPI() {
    install(OpenApi){
        outputFormat = OutputFormat.YAML
        tags {
            tag("Match"){
                description = "Matches"
            }
            tag("User"){
                description = "Users"

            }
        }
    }
    routing{
        route("openapi.yaml") {
            openApi()
        }
    }
}