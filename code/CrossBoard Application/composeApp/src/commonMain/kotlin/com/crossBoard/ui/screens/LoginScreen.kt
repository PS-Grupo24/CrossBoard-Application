package com.crossBoard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.crossBoard.model.AuthState
import com.crossBoard.ui.components.PasswordTextField
import com.crossBoard.utils.CustomColor

/**
 * Responsible for the display of the login elements.
 * @param authState The current authentication state.
 * @param onLoginUsernameChange The action to perform when username text field is updated.
 * @param textFieldColors The `TextFieldColors` for the Text field elements.
 * @param onLoginPasswordChange The action to perform when the password text field is updated.
 * @param onLoginClick The action to perform when the login button is clicked.
 * @param onSwitchScreen The action to perform when switching between screens.
 * @param onMaintainSession The action to perform when the maintain session check box is clicked.
 */
@Composable
fun LoginScreen(
    authState: AuthState,
    onLoginUsernameChange: (String) -> Unit,
    textFieldColors: TextFieldColors,
    onLoginPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSwitchScreen: (Boolean) -> Unit,
    onMaintainSession: (Boolean) -> Unit,
    ) {
        OutlinedTextField(
            value = authState.loginUsernameInput,
            onValueChange = onLoginUsernameChange,
            label = { Text("Username") },
            isError = authState.errorMessage != null,
            singleLine = true,
            colors = textFieldColors,
        )
        Spacer(Modifier.height(8.dp))
        PasswordTextField(
            value = authState.loginPasswordInput,
            errorMessage = authState.errorMessage,
            onValueChange = onLoginPasswordChange,
            textFieldColors = textFieldColors
        )
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = authState.maintainSession,
                onCheckedChange = { onMaintainSession(!authState.maintainSession) }
            )
            Text(
                text = "Maintain Session",
                modifier = Modifier.padding(start = 8.dp),
                color = CustomColor.LightBrown.value
            )
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onLoginClick,
            enabled = !authState.isLoading,
            colors = ButtonDefaults.textButtonColors(backgroundColor = Color(0xFFB57B5C)))
        {
            Text("Login", color = Color.White)
        }
        TextButton(onClick = { onSwitchScreen(false) }, enabled = !authState.isLoading) {
            Text("Don't have an account? Register", color = CustomColor.DarkBrown.value)
        }
}