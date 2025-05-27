package com.crossBoard.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Button
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.crossBoard.ApiClient
import com.crossBoard.domain.NormalUser
import com.crossBoard.domain.UserState
import com.crossBoard.ui.screens.AuthenticationScreen
import com.crossBoard.ui.viewModel.AuthViewModel
import com.russhwolf.settings.Settings

@Composable
fun CrossBoardApplication(client: ApiClient, settings: Settings) {
    val authViewModel = remember { AuthViewModel(client, settings) }
    DisposableEffect(Unit) {
        onDispose {
            authViewModel.clear()
        }
    }
    authViewModel.checkSession()
    val authState by authViewModel.authState.collectAsState()
    if (authState.isAuthenticated) {
        val user = authState.user

        if (user != null) {
            if (user is NormalUser && user.state == UserState.BANNED){
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: User Banned. Please logout.", color = Color.Red)
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = { authViewModel.logout() }) { Text("Logout") }
                }
            }
            else MainMenu(
                client = client,
                user = user,
                onLogout = authViewModel::logout,
            )
        }
        else{
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: Authentication Failed. Please logout.", color = Color.Red)
                Spacer(Modifier.height(20.dp))
                Button(onClick = { authViewModel.logout() }) { Text("Logout") }
            }
        }

    }
    else{

        if (authState.playMatch) {
            SinglePlayerMatch(
                null,
                ongoBack = { authViewModel.playMatch(false) }
            )
        }
        else{
            AuthenticationScreen(
                authState = authState,
                onLoginUsernameChange = authViewModel::updateLoginUsername,
                onLoginPasswordChange = authViewModel::updateLoginPassword,

                onRegisterEmailChange = authViewModel::updateRegisterEmail,
                onRegisterPasswordChange = authViewModel::updateRegisterPassword,
                onRegisterUsernameChange = authViewModel::updateRegisterUsername,

                onLoginClick = authViewModel::login,
                onRegisterClick = authViewModel::register,
                onSwitchScreen = authViewModel::showLoginScreen,
                onMaintainSession = authViewModel::maintainSession,
                onPlayMatch = authViewModel::playMatch
            )
        }
    }
}