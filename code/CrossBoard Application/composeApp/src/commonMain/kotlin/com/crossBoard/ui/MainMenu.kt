package com.crossBoard.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.crossBoard.ApiClient
import com.crossBoard.model.MainScreen
import com.crossBoard.model.SubScreen
import com.crossBoard.model.UserInfoState
import com.crossBoard.ui.screens.MainMenuScreen
import com.crossBoard.ui.screens.MyAlertDialog
import com.crossBoard.ui.screens.ProfileScreen
import com.crossBoard.ui.viewModel.MainMenuViewModel
import com.crossBoard.utils.CustomColor
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun MainMenu(
    client: ApiClient,
    userToken: String,
    currentUserId: Int,
    userInfoState: UserInfoState,
    onGetUserInfo: () -> Unit,
    onLogout: () -> Unit,
){
    val vm = remember { MainMenuViewModel() }
    val mainMenuState  by vm.mainMenuState.collectAsState()
    DisposableEffect(Unit){
        onDispose {
            vm.clear()
        }
    }
    var showConfirmDialog by remember { mutableStateOf(false) }
    vm.setTobBarMessage("Welcome, ${userInfoState.username}!")
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
            TopAppBar(
                title = {
                    Text(
                        text = mainMenuState.topBarMessage, color = Color.White
                    )
                        },
                navigationIcon = if (mainMenuState.currentMainScreen != MainScreen.MainMenu && mainMenuState.currentSubScreen != SubScreen.Match )
                {
                    {
                        IconButton(
                            onClick = {
                                vm.goToMainMenu(userInfoState.username)
                            }
                        )
                        {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                }
                else null,
                actions = {
                    if (mainMenuState.currentSubScreen != SubScreen.Match){
                        Button(
                            onClick = {
                                vm.goToProfile(userInfoState.username)
                            },
                            colors = ButtonDefaults.buttonColors(CustomColor.LightBrown.value)
                        ) {
                            Text("Profile", color = Color.White)
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {showConfirmDialog = true},
                            colors = ButtonDefaults.textButtonColors(backgroundColor = CustomColor.LightBrown.value)
                        ){ Text("Log out", color = Color.White) } }
                    },
                backgroundColor = CustomColor.DarkBrown.value
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
                            onFindMatchClicked = { vm.goToGameFlow("Multiplayer Match") },
                        )
                    }
                    MainScreen.Profile -> {
                        onGetUserInfo()
                        ProfileScreen(
                            userInfoState
                        )
                    }
                    MainScreen.GameFlow -> {
                        GameFlow(
                            onFindMatch = vm::goToFindMatch,
                            onMatch = vm::goToMatch,
                            client = client,
                            userToken = userToken,
                            currentUserId = currentUserId,
                        )

                    }
                }
            }
        }

    }
}