package com.crossBoard.ui.viewModel

import androidx.lifecycle.ViewModel
import com.crossBoard.model.MainScreen
import com.crossBoard.model.MainMenuState
import com.crossBoard.model.SubScreen
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainMenuViewModel(
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
): Clearable {
    private val viewModelScope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val _mainMenuState = MutableStateFlow(MainMenuState(topBarMessage = "Welcome!"))
    val mainMenuState: StateFlow<MainMenuState> = _mainMenuState.asStateFlow()

    fun navigateTo(destination: MainScreen) {
        _mainMenuState.update { it.copy(currentMainScreen = destination) }
    }

    fun setTobBarMessage(message: String) {
        _mainMenuState.update { it.copy(topBarMessage = message) }
    }

    fun navigateTo(destination: SubScreen) {
        _mainMenuState.update { it.copy(currentSubScreen = destination) }
    }

    fun goToMainMenu(topBarMessage: String? = null) {
        if (topBarMessage != null) setTobBarMessage(topBarMessage)
        navigateTo(MainScreen.MainMenu)
    }
    fun goToProfile(topBarMessage: String? = null) {
        if (topBarMessage != null) setTobBarMessage(topBarMessage)
        navigateTo(MainScreen.Profile)
    }
    fun goToGameFlow(topBarMessage: String? = null) {
        if (topBarMessage != null) setTobBarMessage(topBarMessage)
        navigateTo(MainScreen.GameFlow)
    }
    fun goToFindMatch(topBarMessage: String = "Multiplayer Match") {
        setTobBarMessage(topBarMessage)
        navigateTo(SubScreen.FindMatch)
    }
    fun goToMatch(topBarMessage: String? = null) {
        if (topBarMessage != null) setTobBarMessage(topBarMessage)
        navigateTo(SubScreen.Match)
    }
    override fun clear() {
        viewModelScope.cancel()
    }
}