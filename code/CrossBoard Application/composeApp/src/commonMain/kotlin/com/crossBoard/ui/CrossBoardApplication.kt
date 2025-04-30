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
import androidx.compose.ui.unit.dp
import com.crossBoard.ApiClient
import com.crossBoard.ui.screens.AuthenticationScreen
import com.crossBoard.ui.viewModel.AuthViewModel
import com.crossBoard.ui.viewModel.UserInfoViewModel

@Composable
fun CrossBoardApplication(client: ApiClient){
    val authViewModel = remember { AuthViewModel(client) }
    val userViewModel = remember { UserInfoViewModel(client) }
    DisposableEffect(Unit) {
        onDispose {
            authViewModel.clear()
            userViewModel.clear()
        }
    }

    val authState by authViewModel.authState.collectAsState()
    val userInfoState by userViewModel.user.collectAsState()

    if (authState.isAuthenticated) {

        val userToken = authState.userToken
        val currentUserId = authState.currentUser?.id

        if (userToken != null && currentUserId != null){
            userViewModel.getUser(currentUserId)
            MainMenu(
                client = client,
                userToken = userToken,
                currentUserId = currentUserId,
                userInfoState = userInfoState,
                onGetUserInfo = {
                    userViewModel.getUser(currentUserId)
                },
                onLogout = authViewModel::logout,
            )
        }
        else{
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: Authentication state inconsistent. Please logout.")
                Spacer(Modifier.height(20.dp))
                Button(onClick = { authViewModel.logout() }) { Text("Logout") }
            }
        }

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
            onSwitchScreen = authViewModel::showLoginScreen
            )
    }
}