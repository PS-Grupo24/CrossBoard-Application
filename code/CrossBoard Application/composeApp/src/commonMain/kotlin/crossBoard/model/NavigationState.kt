package crossBoard.model

import androidx.compose.runtime.Immutable

@Immutable
data class NavigationState(
    val currentScreen: MainScreen = MainScreen.MainMenu
)