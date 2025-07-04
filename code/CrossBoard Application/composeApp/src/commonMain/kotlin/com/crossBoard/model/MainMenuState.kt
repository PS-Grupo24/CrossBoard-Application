package com.crossBoard.model

import androidx.compose.runtime.Immutable

/**
 * MainMenuState responsible for tracking the resources for the main menu functionality.
 * @param currentMainScreen The current `MainScreen` to be displayed.
 * @param currentSubScreen The current `SubScreen` inside a `MainScreen`.
 * @param topBarMessage The message to be displayed in the top bar.
 */
@Immutable
data class MainMenuState(
    val currentMainScreen: MainScreen = MainScreen.MainMenu,
    val currentSubScreen: SubScreen? = null,
    val topBarMessage: String
)