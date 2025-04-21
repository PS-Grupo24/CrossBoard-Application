package org.example.project.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
                isError = authState.errorMessage != null
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = authState.loginPasswordInput,
                onValueChange = onLoginPasswordChange,
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = authState.errorMessage != null
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = onLoginClick, enabled = !authState.isLoading) {
                Text("Login")
            }
            TextButton(onClick = { onSwitchScreen(false) }, enabled = !authState.isLoading) { // Switch to Register
                Text("Don't have an account? Register")
            }

        } else {
            OutlinedTextField(
                value = authState.registerUsernameInput,
                onValueChange = onRegisterUsernameChange,
                label = { Text("Username") },
                isError = authState.errorMessage != null
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = authState.registerEmailInput,
                onValueChange = onRegisterEmailChange,
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = authState.errorMessage != null
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = authState.registerPasswordInput,
                onValueChange = onRegisterPasswordChange,
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = authState.errorMessage != null
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRegisterClick, enabled = !authState.isLoading) {
                Text("Register")
            }
            TextButton(onClick = { onSwitchScreen(true) }, enabled = !authState.isLoading) { // Switch to Login
                Text("Already have an account? Login")
            }
        }

        Spacer(Modifier.height(16.dp))

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