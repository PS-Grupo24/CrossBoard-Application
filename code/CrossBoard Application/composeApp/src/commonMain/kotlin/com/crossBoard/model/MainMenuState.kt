package com.crossBoard.model

import androidx.compose.runtime.Immutable

@Immutable
data class MainMenuState(
    val currentMainScreen: MainScreen = MainScreen.MainMenu,
    val currentSubScreen: SubScreen? = null,
    val topBarMessage: String
)