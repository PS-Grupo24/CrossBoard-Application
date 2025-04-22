package org.example.project

import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.ktor.client.engine.js.*
import kotlinx.browser.document
import org.example.project.utils.createHttpClient

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val host = getHost()
    ComposeViewport(document.body!!) {
        App(client = remember {
            ApiClient(createHttpClient(JsClient().create()), host)
        })
    }
}