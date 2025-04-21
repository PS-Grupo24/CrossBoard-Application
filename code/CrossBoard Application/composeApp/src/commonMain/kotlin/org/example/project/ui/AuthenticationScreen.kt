package org.example.project.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.viewModel.AuthState

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
    onSwitchScreen: (showLogin: Boolean) -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            if (authState.isLoginScreenVisible) "Login" else "Register",
            style = MaterialTheme.typography.h4
        )
        Spacer(Modifier.height(24.dp))

        if (authState.isLoginScreenVisible) {
            OutlinedTextField(
                value = authState.loginUsernameInput,
                onValueChange = onLoginUsernameChange,
                label = { Text("Username") },
                isError = authState.errorMessage != null,
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = authState.loginPasswordInput,
                onValueChange = onLoginPasswordChange,
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = authState.errorMessage != null,
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = onLoginClick, enabled = !authState.isLoading) {
                Text("Login")
            }
            TextButton(onClick = { onSwitchScreen(false) }, enabled = !authState.isLoading) {
                Text("Don't have an account? Register")
            }

        } else {

            OutlinedTextField(
                value = authState.registerUsernameInput,
                onValueChange = onRegisterUsernameChange,
                label = { Text("Username") },
                isError = authState.errorMessage != null,
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = authState.registerEmailInput,
                onValueChange = onRegisterEmailChange,
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = authState.errorMessage != null,
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))


            OutlinedTextField(
                value = authState.registerPasswordInput,
                onValueChange = onRegisterPasswordChange,
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = authState.errorMessage != null,
                singleLine = true
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


            Spacer(Modifier.height(16.dp))
            Button(onClick = onRegisterClick, enabled = !authState.isLoading) {
                Text("Register")
            }
            TextButton(onClick = { onSwitchScreen(true) }, enabled = !authState.isLoading) {
                Text("Already have an account? Login")
            }
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

@Composable
fun PasswordRequirement(
    text: String,
    isMet: Boolean
) {
    Text(
        text = "• $text",
        color = if (isMet) MaterialTheme.colors.primary
        else LocalContentColor.current.copy(alpha = ContentAlpha.medium),
        style = MaterialTheme.typography.caption,
        fontSize = 12.sp
    )
}