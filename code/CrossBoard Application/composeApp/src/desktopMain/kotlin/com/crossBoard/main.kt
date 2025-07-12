package com.crossBoard

import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import io.ktor.client.engine.okhttp.*
import com.crossBoard.utils.createHttpClient

/**
 * Main function to launch the desktop app.
 */
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "CrossBoard Application",
        state = WindowState(width = 1000.dp, height = 900.dp),
        resizable = false,
    ) {
        val host = getHost()
        val settings = getSettings()
        App(
            client = remember {
                ApiClient(createHttpClient(OkHttp.create()), host)
            },
            settings
        )
    }
}

