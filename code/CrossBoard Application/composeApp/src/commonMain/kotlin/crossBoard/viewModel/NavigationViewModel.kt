package crossBoard.viewModel

import crossBoard.model.MainScreen
import crossBoard.model.NavigationState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationViewModel(
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
): Clearable {
    private val viewModelScope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val _navigationState = MutableStateFlow(NavigationState())
    val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

    fun navigateTo(destination: MainScreen) {
        _navigationState.value = NavigationState(currentScreen = destination)
    }

    fun goToMainMenu() = navigateTo(MainScreen.MainMenu)
    fun goToProfile() = navigateTo(MainScreen.Profile)
    fun goToGameFlow() = navigateTo(MainScreen.GameFlow)

    override fun clear() {
        viewModelScope.cancel()
        println("NavigationViewModel cleared.")
    }
}