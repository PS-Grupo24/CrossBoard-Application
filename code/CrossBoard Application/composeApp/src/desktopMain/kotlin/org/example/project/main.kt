package org.example.project

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.ktor.client.engine.okhttp.*

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "CrossBoard Application",
    ) {
        App(
            client = remember {
                MatchClient(createHttpClient(OkHttp.create()))
            }
        )
    }
}