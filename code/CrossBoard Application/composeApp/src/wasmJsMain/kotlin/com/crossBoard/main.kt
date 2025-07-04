package com.crossBoard

import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.ktor.client.engine.js.*
import kotlinx.browser.document
import com.crossBoard.utils.createHttpClient

/**
 * Main function to launch the app for browser.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val host = getHost()
    val settings = getSettings()
    ComposeViewport(document.body!!) {
        App(
            client = remember {
                ApiClient(createHttpClient(JsClient().create()), host)
            },
            settings
        )
    }
}