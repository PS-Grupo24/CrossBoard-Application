package com.crossBoard

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.ktor.client.engine.okhttp.*
import com.crossBoard.utils.createHttpClient

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "CrossBoard Application",
    ) {
        val host = getHost()
        App(
            client = remember {
                ApiClient(createHttpClient(OkHttp.create()), host)
            }
        )
    }
}

