package org.example.project

import androidx.compose.runtime.*
import org.example.project.ui.ticTacToeApp
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App(client: ApiClient) {
    ticTacToeApp(client)
}