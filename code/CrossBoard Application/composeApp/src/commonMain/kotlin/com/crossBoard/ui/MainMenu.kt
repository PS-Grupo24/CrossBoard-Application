package com.crossBoard.ui


import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import com.crossBoard.ApiClient
import com.crossBoard.domain.Admin
import com.crossBoard.domain.User
import com.crossBoard.model.MainScreen
import com.crossBoard.model.SubScreen
import com.crossBoard.ui.screens.MainMenuScreen
import com.crossBoard.ui.components.MyAlertDialog
import com.crossBoard.ui.components.SlideTransition
import com.crossBoard.ui.components.TopBar
import com.crossBoard.ui.screens.ProfileScreen
import com.crossBoard.ui.viewModel.MainMenuViewModel

/**
 * Activity for the MainMenu.
 * It allows for the user to navigate between the different app functionalities.
 * @param client The client to perform requests.
 * @param user The logged user.
 * @param onLogout The action to perform on logout.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainMenu(
     client: ApiClient,
     user: User,
     onLogout: () -> Unit,
) {
    val vm = remember { MainMenuViewModel() }
    val mainMenuState  by vm.mainMenuState.collectAsState()
    vm.setTobBarMessage("Welcome, ${user.username.value}!")

    DisposableEffect(Unit){
        onDispose {
            vm.clear()
        }
    }

    var showConfirmDialog by remember { mutableStateOf(false) }
    BackHandler(true) {
        showConfirmDialog = true
    }
    if (showConfirmDialog) {
        MyAlertDialog(
            {showConfirmDialog = false},
            "Confirm Logout",
            "Are you sure you want to logout?",
            onConfirm = {
                showConfirmDialog = false
                onLogout()
            },
            confirmText = "Yes, Logout",
            onDismiss = {showConfirmDialog = false},
            dismissText = "Cancel",
        )
    }
    val showTopBar = when (mainMenuState.currentSubScreen) {
        SubScreen.Match, SubScreen.MatchOver -> false
        else -> true
    }
    Scaffold(
        topBar = {
            if(showTopBar) {
                TopBar(
                    user, mainMenuState, vm
                ) { showConfirmDialog = true }
            }

        }
    ) { paddingValues ->
        SlideTransition(
            targetState = mainMenuState.currentMainScreen,
        ){
                currentScreen ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                when(currentScreen){
                    MainScreen.MainMenu -> {
                        MainMenuScreen(
                            user = user,
                            onSinglePlayerClicked = { vm.goToSinglePlayer("Single Player") },
                            onFindMatchClicked = { vm.goToGameFlow("Multiplayer Match") },
                            onCheckStatsClicked = {vm.goToStatistics("Match History")},
                            onAdminPanelClicked = {vm.goToAdminPanel("Admin Panel")}
                        )
                    }
                    MainScreen.Profile -> {
                        ProfileScreen(
                            user,
                            onBack = { vm.goToMainMenu(user.username.value)}
                        )
                    }
                    MainScreen.GameFlow -> {
                        MultiPlayerMatchFlow(
                            onFindMatch = vm::goToFindMatch,
                            onMatch = vm::goToMatch,
                            client = client,
                            userToken = user.token.value,
                            currentUserId = user.id,
                            onMatchOver = vm::goToMatchOver,
                            onBack = { vm.goToMainMenu(user.username.value) }
                        )
                    }
                    MainScreen.Statistics -> {
                        Statistics(
                            user,
                            client,
                            onBack = { vm.goToMainMenu(user.username.value)}
                        )
                    }
                    MainScreen.SinglePlayer -> {
                        SinglePlayerMatch(
                            user,
                            ongoBack = { vm.goToMainMenu(user.username.value) }
                        )
                    }
                    MainScreen.AdminPanel -> {
                        AdminPanel(
                            (user as Admin),
                            client,
                            onBack = { vm.goToMainMenu(user.username.value) }
                        )
                    }
                }
            }
        }

    }
}