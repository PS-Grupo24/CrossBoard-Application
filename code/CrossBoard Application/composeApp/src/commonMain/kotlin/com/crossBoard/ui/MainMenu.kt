package com.crossBoard.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import com.crossBoard.ApiClient
import com.crossBoard.domain.Admin
import com.crossBoard.domain.User
import com.crossBoard.model.MainScreen
import com.crossBoard.ui.screens.MainMenuScreen
import com.crossBoard.ui.components.MyAlertDialog
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
    Scaffold(
        topBar = {
            TopBar(
                user, mainMenuState, vm, {showConfirmDialog = true}
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = mainMenuState.currentMainScreen,
            label = "MainMenuScreenFlow",
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
                            user
                        )
                    }
                    MainScreen.GameFlow -> {
                        MultiPlayerMatchFlow(
                            onFindMatch = vm::goToFindMatch,
                            onMatch = vm::goToMatch,
                            client = client,
                            userToken = user.token.value,
                            currentUserId = user.id,
                            onMatchOver = vm::goToMatchOver
                        )
                    }
                    MainScreen.Statistics -> {
                        Statistics(
                            user,
                            client
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
                            client
                        )
                    }
                }
            }
        }

    }
}