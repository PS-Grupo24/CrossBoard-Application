package com.crossBoard

import androidx.compose.runtime.*
import com.crossBoard.ui.CrossBoardApplication
import com.russhwolf.settings.Settings
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App(client: ApiClient, settings: Settings) {
    CrossBoardApplication(client, settings)
}