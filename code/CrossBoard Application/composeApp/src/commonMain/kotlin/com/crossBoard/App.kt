package com.crossBoard

import androidx.compose.runtime.*
import com.crossBoard.ui.CrossBoardApplication
import com.russhwolf.settings.Settings

/**
 * Main entry point for the CrossBoard application.
 * @param client The API client used for network operations.
 * @param settings The settings used for storing user preferences.
 */
@Composable
fun App(client: ApiClient, settings: Settings) {
    CrossBoardApplication(client, settings)
}