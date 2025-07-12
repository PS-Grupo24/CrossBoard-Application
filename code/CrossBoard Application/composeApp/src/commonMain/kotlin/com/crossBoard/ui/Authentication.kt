package com.crossBoard.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.crossBoard.model.AuthState
import com.crossBoard.ui.screens.AuthenticationScreen

data class Authentication(
    val authState: AuthState,
    val onLoginUsernameChange: (String) -> Unit,
    val onLoginPasswordChange: (String) -> Unit,
    val onRegisterUsernameChange: (String) -> Unit,
    val onRegisterEmailChange: (String) -> Unit,
    val onRegisterPasswordChange: (String) -> Unit,
    val onLoginClick: () -> Unit,
    val onRegisterClick: () -> Unit,
    val onSwitchScreen: (showLogin: Boolean) -> Unit,
    val onMaintainSession: (Boolean) -> Unit,
    val onPlayMatch: (play: Boolean) -> Unit,
) : Screen {
    @Composable
    override fun Content() {
        AuthenticationScreen(
            authState = authState,
            onLoginUsernameChange = onLoginUsernameChange,
            onLoginPasswordChange = onLoginPasswordChange,
            onRegisterUsernameChange = onRegisterUsernameChange,
            onRegisterEmailChange = onRegisterEmailChange,
            onRegisterPasswordChange = onRegisterPasswordChange,
            onLoginClick = onLoginClick,
            onRegisterClick = onRegisterClick,
            onSwitchScreen = onSwitchScreen,
            onMaintainSession = onMaintainSession,
            onPlayMatch = onPlayMatch,
        )
    }
}
