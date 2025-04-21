package org.example.project

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.ktor.client.engine.okhttp.*
import org.example.project.utils.createHttpClient

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "CrossBoard Application",
    ) {
        App(
            client = remember {
                ApiClient(createHttpClient(OkHttp.create()))
            }
        )
    }
}