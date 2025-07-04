package com.crossBoard.ui.viewModel

import com.crossBoard.interfaces.Clearable
import com.crossBoard.model.MainScreen
import com.crossBoard.model.MainMenuState
import com.crossBoard.model.SubScreen
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * viewModel "MainMenuViewModel" responsible for managing the navigation between screens.
 * @param mainDispatcher The coroutine dispatcher; `Dispatcher.Main` by default.
 */
class MainMenuViewModel(
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
): Clearable {
    private val viewModelScope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val _mainMenuState = MutableStateFlow(MainMenuState(topBarMessage = "Welcome!"))

    /**
     * Responsible for collecting the flow of `MainMenuState`
     */
    val mainMenuState: StateFlow<MainMenuState> = _mainMenuState.asStateFlow()

    /**
     * Function "navigateTo" responsible for changing the current main screen into a given destination.
     * @param destination The new MainScreen to navigate to.
     */
    fun navigateTo(destination: MainScreen) {
        _mainMenuState.update { it.copy(currentMainScreen = destination) }
    }

    /**
     * Function "setTopBarMessage" responsible for managing the message to be displayed in the top bar.
     * @param message The new message to be displayed.
     */
    fun setTobBarMessage(message: String) {
        _mainMenuState.update { it.copy(topBarMessage = message) }
    }

    /**
     * Function "navigateTo" responsible for changing the current sub screen into a given destination.
     * @param destination The new Subscreen to navigate to.
     */
    fun navigateTo(destination: SubScreen) {
        _mainMenuState.update { it.copy(currentSubScreen = destination) }
    }

    /**
     * Function "goToMainMenu" navigates to the main menu.
     * @param topBarMessage The message to be displayed in the top bar after the navigation.
     */
    fun goToMainMenu(topBarMessage: String? = null) {
        if (topBarMessage != null) setTobBarMessage(topBarMessage)
        navigateTo(MainScreen.MainMenu)
    }

    /**
     * Function "goToProfile" navigates to the profile activity.
     * @param topBarMessage The message to be displayed in the top bar after the navigation.
     */
    fun goToProfile(topBarMessage: String? = null) {
        if (topBarMessage != null) setTobBarMessage(topBarMessage)
        navigateTo(MainScreen.Profile)
    }

    /**
     * Function "goToStatistics" navigates to the statistics activity.
     * @param topBarMessage The message to be displayed in the top bar after the navigation.
     */
    fun goToStatistics(topBarMessage: String? = null) {
        if (topBarMessage != null) setTobBarMessage(topBarMessage)
        navigateTo(MainScreen.Statistics)
    }

    /**
     * Function "goToAdminPanel" navigates to the admin panel.
     * @param topBarMessage The message to be displayed in the top bar after the navigation.
     */
    fun goToAdminPanel(topBarMessage: String? = null) {
        if (topBarMessage != null) setTobBarMessage(topBarMessage)
        navigateTo(MainScreen.AdminPanel)
    }

    /**
     * Function "goToSinglePlayer" navigates to the single player activity.
     * @param topBarMessage The message to be displayed in the top bar after the navigation.
     */
    fun goToSinglePlayer(topBarMessage: String? = null) {
        if (topBarMessage != null) setTobBarMessage(topBarMessage)
        navigateTo(MainScreen.SinglePlayer)
    }

    /**
     * Function "goToGameFlow" navigates to the multiplayer match activity.
     * @param topBarMessage The message to be displayed in the top bar after the navigation.
     */
    fun goToGameFlow(topBarMessage: String? = null) {
        if (topBarMessage != null) setTobBarMessage(topBarMessage)
        navigateTo(MainScreen.GameFlow)
    }
    /**
     * Function "goToFindMatch" navigates to the find match subscreen.
     * @param topBarMessage The message to be displayed in the top bar after the navigation.
     */
    fun goToFindMatch(topBarMessage: String = "Multiplayer Match") {
        setTobBarMessage(topBarMessage)
        navigateTo(SubScreen.FindMatch)
    }
    /**
     * Function "goToMatch" navigates to the match subscreen.
     * @param topBarMessage The message to be displayed in the top bar after the navigation.
     */
    fun goToMatch(topBarMessage: String? = null) {
        if (topBarMessage != null) setTobBarMessage(topBarMessage)
        navigateTo(SubScreen.Match)
    }

    /**
     * Function "clear" responsible for the cleanup of the viewModel.
     * Cancels the viewModel scope;
     */
    override fun clear() {
        viewModelScope.cancel()
    }
}