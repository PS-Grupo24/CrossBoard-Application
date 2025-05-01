package com.crossBoard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crossBoard.model.AuthState
import com.crossBoard.utils.CustomColor

@Composable
fun AuthenticationScreen(
    authState: AuthState,
    onLoginUsernameChange: (String) -> Unit,
    onLoginPasswordChange: (String) -> Unit,
    onRegisterUsernameChange: (String) -> Unit,
    onRegisterEmailChange: (String) -> Unit,
    onRegisterPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onSwitchScreen: (showLogin: Boolean) -> Unit,
    onMaintainSession: (Boolean) -> Unit
){
    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        unfocusedBorderColor = CustomColor.LightBrown.value,
        focusedBorderColor = CustomColor.DarkBrown.value,
        unfocusedLabelColor = CustomColor.LightBrown.value,
        focusedLabelColor = CustomColor.DarkBrown.value,
    )
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,

    ) {
        Text(
            if (authState.isLoginScreenVisible) "Login" else "Register",
            style = MaterialTheme.typography.h4,
            color = CustomColor.LightBrown.value,
        )
        Spacer(Modifier.height(24.dp))

        if (authState.isLoginScreenVisible) {
            LoginScreen(
                authState,
                onLoginUsernameChange,
                textFieldColors,
                onLoginPasswordChange,
                onLoginClick,
                onSwitchScreen,
                onMaintainSession
            )
        } else {
            RegisterScreen(
                authState,
                onRegisterUsernameChange,
                onRegisterPasswordChange,
                onRegisterEmailChange,
                textFieldColors,
                onRegisterClick,
                onSwitchScreen,
                onMaintainSession
            )
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier.heightIn(min = 48.dp),
            contentAlignment = Alignment.Center
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator()
            } else {
                authState.errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colors.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

