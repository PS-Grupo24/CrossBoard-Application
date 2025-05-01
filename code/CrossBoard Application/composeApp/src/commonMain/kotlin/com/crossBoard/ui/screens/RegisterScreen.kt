package com.crossBoard.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crossBoard.model.AuthState
import com.crossBoard.utils.CustomColor

@Composable
fun RegisterScreen(
    authState: AuthState,
    onRegisterUsernameChange: (String) -> Unit,
    onRegisterPasswordChange: (String) -> Unit,
    onRegisterEmailChange: (String) -> Unit,
    textFieldColors: TextFieldColors,
    onRegisterClick: () -> Unit,
    onSwitchScreen: (Boolean) -> Unit,
    onMaintainSession: (Boolean) -> Unit,
){
    OutlinedTextField(
        value = authState.registerUsernameInput,
        onValueChange = onRegisterUsernameChange,
        label = { Text("Username") },
        isError = authState.errorMessage != null,
        singleLine = true,
        colors = textFieldColors,
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = authState.registerEmailInput,
        onValueChange = onRegisterEmailChange,
        label = { Text("Email") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        isError = authState.errorMessage != null,
        singleLine = true,
        colors = textFieldColors,
    )
    Spacer(Modifier.height(8.dp))


    OutlinedTextField(
        value = authState.registerPasswordInput,
        onValueChange = onRegisterPasswordChange,
        label = { Text("Password") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        isError = authState.errorMessage != null,
        singleLine = true,
        colors = textFieldColors,
    )


    if (!authState.isLoginScreenVisible && authState.registerPasswordInput.isNotEmpty()) {
        Spacer(Modifier.height(8.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(0.8f)) {

            val password = authState.registerPasswordInput
            val hasMinLength = remember(password) { password.length >= 8 }
            val hasUppercase = remember(password) { password.contains(Regex("[A-Z]")) }
            val hasLowercase = remember(password) { password.contains(Regex("[a-z]")) }
            val hasNumber = remember(password) { password.contains(Regex("[0-9]")) }
            val hasSpecial = remember(password) { password.contains(Regex("[!@#\$%^&*]")) }

            PasswordRequirement(text = "At least 8 characters", isMet = hasMinLength)
            PasswordRequirement(text = "At least one uppercase letter", isMet = hasUppercase)
            PasswordRequirement(text = "At least one lowercase letter", isMet = hasLowercase)
            PasswordRequirement(text = "At least one number", isMet = hasNumber)
            PasswordRequirement(text = "At least one special character (!@#\$%^&*)", isMet = hasSpecial)
        }
    }

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
    Button(onClick = onRegisterClick, enabled = !authState.isLoading, colors = ButtonDefaults.textButtonColors(backgroundColor = CustomColor.LightBrown.value)) {
        Text("Register", color = Color.White)
    }
    TextButton(onClick = { onSwitchScreen(true) }, enabled = !authState.isLoading) {
        Text("Already have an account? Login", color = CustomColor.DarkBrown.value)
    }
}

@Composable
private fun PasswordRequirement(
    text: String,
    isMet: Boolean
) {
    Text(
        text = "• $text",
        color = if (isMet) CustomColor.DarkBrown.value
        else Color.Red,
        style = MaterialTheme.typography.caption,
        fontSize = 12.sp
    )
}