package crossBoard.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import crossBoard.model.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoggedInContent(
    navigationState: NavigationState,
    matchUiState: MatchUiState,
    authState: AuthState,
    userInfoState: UserInfoState,

    onGetUserInfo: () -> Unit,

    onNavigateToFindMatch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToMainMenu: () -> Unit,

    onGameTypeChange: (String) -> Unit,
    onFindMatch: () -> Unit,
    onCancelSearch: () -> Unit,
    onMakeMove: (row: Int, column: Int) -> Unit,
    onForfeit: () -> Unit,
    onResetMatch: () -> Unit,
    onLogout: () -> Unit,
){
    val currentUser = authState.currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome!") },
                navigationIcon = if (navigationState.currentScreen != MainScreen.MainMenu) {
                    { IconButton(onClick = onNavigateToMainMenu) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
                } else null,
                actions = { /* ... Logout Button ... */ }
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = navigationState.currentScreen,
            label = "LoggedInScreenFlow",
        ){
            currentScreen ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                when(currentScreen){
                    MainScreen.MainMenu -> {
                        MainMenuScreen(
                            onFindMatchClicked = onNavigateToFindMatch,
                            onProfileClicked = onNavigateToProfile,
                            onLogoutClicked = onLogout,
                        )
                    }
                    MainScreen.Profile -> {
                        onGetUserInfo()
                        ProfileScreen(
                            userInfoState
                        )
                    }
                    MainScreen.GameFlow -> {
                        val currentMatch = matchUiState.currentMatch
                        if (currentMatch == null)
                            FindMatchScreen(
                                selectedGameTypeValue = matchUiState.gameTypeInput,
                                isLoading = matchUiState.isLoading,
                                errorMessage = matchUiState.errorMessage,
                                onGameTypeChange = onGameTypeChange,
                                onFindMatchClick = onFindMatch
                            )
                        else
                            GameFlowScreen(
                                matchUiState = matchUiState,
                                currentUserId = authState.currentUser?.id,
                                onCancelSearch = onCancelSearch,
                                onMakeMove = onMakeMove,
                                onForfeit = onForfeit,
                                onResetMatch = onResetMatch,
                            )
                    }
                }
            }
        }

    }
}