package com.crossBoard

import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.crossBoard.ui.CrossBoardApplication
import com.russhwolf.settings.Settings


@Composable
fun App(client: ApiClient, settings: Settings) {
    Navigator(CrossBoardApplication(client, settings)){navigator->
        SlideTransition(navigator)
    }
}