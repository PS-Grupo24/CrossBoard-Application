package com.crossBoard

import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.crossBoard.ui.CrossBoardApplication
import com.russhwolf.settings.Settings

/**
 * Main entry point for the CrossBoard application.
 * @param client The API client used for network operations.
 * @param settings The settings used for storing user preferences.
 */
@Composable
fun App(client: ApiClient, settings: Settings) {
    Navigator(CrossBoardApplication(client, settings)){navigator->
        SlideTransition(navigator)
    }
}