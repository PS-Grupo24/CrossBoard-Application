package com.crossBoard.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crossBoard.model.AuthState
import com.crossBoard.ui.components.PasswordTextField
import com.crossBoard.ui.components.SlideTransition
import com.crossBoard.utils.CustomColor

/**
 * A polished, unified screen for user authentication (Login and Register).
 * Features a modern card-based layout and smooth animations.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AuthenticationScreen(
    authState: AuthState,
    onLoginUsernameChange: (String) -> Unit,
    onLoginPasswordChange: (String) -> Unit,
    onRegisterEmailChange: (String) -> Unit,
    onRegisterPasswordChange: (String) -> Unit,
    onRegisterUsernameChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onMaintainSession: (Boolean) -> Unit,
    onPlayMatch: () -> Unit,
    onSwitchScreen: (Boolean) -> Unit,
) {
    val showLogin = authState.isLoginScreenVisible

    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        unfocusedBorderColor = CustomColor.LightBrown.value,
        focusedBorderColor = CustomColor.DarkBrown.value,
        unfocusedLabelColor = CustomColor.LightBrown.value,
        focusedLabelColor = CustomColor.DarkBrown.value,
        textColor = CustomColor.DarkBrown.value,
        cursorColor = CustomColor.DarkBrown.value,
        leadingIconColor = CustomColor.DarkBrown.value.copy(alpha = 0.5f),
        //focusedLeadingIconColor = CustomColor.DarkBrown.value
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "CrossBoard",
            style = MaterialTheme.typography.h3,
            fontWeight = FontWeight.Bold,
            color = CustomColor.DarkBrown.value
        )
        Text(
            text = "Your Next Move Awaits",
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )

        Spacer(Modifier.height(32.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 8.dp,
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SlideTransition(
                    targetState = showLogin,
                ) { isLoginScreen ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isLoginScreen) {
                            LoginForm(
                                authState = authState,
                                onLoginUsernameChange = onLoginUsernameChange,
                                onLoginPasswordChange = onLoginPasswordChange,
                                textFieldColors = textFieldColors
                            )
                        } else {
                            RegisterForm(
                                authState = authState,
                                onRegisterUsernameChange = onRegisterUsernameChange,
                                onRegisterEmailChange = onRegisterEmailChange,
                                onRegisterPasswordChange = onRegisterPasswordChange,
                                textFieldColors = textFieldColors
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = authState.maintainSession,
                        onCheckedChange = onMaintainSession,
                        colors = CheckboxDefaults.colors(checkedColor = CustomColor.DarkBrown.value)
                    )
                    Text(
                        text = "Stay logged in",
                        modifier = Modifier.padding(start = 8.dp),
                        color = MaterialTheme.colors.onSurface
                    )
                }

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { if (showLogin) onLoginClick() else onRegisterClick() },
                    enabled = !authState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = CustomColor.DarkBrown.value,
                        contentColor = Color.White
                    )
                ) {
                    AnimatedContent(targetState = authState.isLoading) { isLoading ->
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(if (showLogin) "Login" else "Create Account", fontSize = 16.sp)
                        }
                    }
                }
                AnimatedVisibility(visible = authState.errorMessage != null && !authState.isLoading) {
                    Text(
                        text = authState.errorMessage ?: "",
                        color = MaterialTheme.colors.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = {onSwitchScreen(!showLogin)}, enabled = !authState.isLoading) {
            Text(
                if (showLogin) "Don't have an account? Register" else "Already have an account? Login",
                color = CustomColor.DarkBrown.value
            )
        }
        OutlinedButton(onClick = onPlayMatch, enabled = !authState.isLoading) {
            Text("Play as Guest", color = CustomColor.DarkBrown.value)
        }
    }
}

/**
 * A private composable containing only the fields for the Login form.
 */
@Composable
private fun LoginForm(
    authState: AuthState,
    onLoginUsernameChange: (String) -> Unit,
    onLoginPasswordChange: (String) -> Unit,
    textFieldColors: TextFieldColors,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = authState.loginUsernameInput,
            onValueChange = onLoginUsernameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Username") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username Icon") },
            isError = authState.errorMessage != null,
            singleLine = true,
            colors = textFieldColors,
        )
        PasswordTextField(
            modifier = Modifier.fillMaxWidth(),
            value = authState.loginPasswordInput,
            onValueChange = onLoginPasswordChange,
            isError = authState.errorMessage != null,
            textFieldColors = textFieldColors
        )
    }
}

/**
 * A private composable containing only the fields for the Register form.
 */
@Composable
private fun RegisterForm(
    authState: AuthState,
    onRegisterUsernameChange: (String) -> Unit,
    onRegisterEmailChange: (String) -> Unit,
    onRegisterPasswordChange: (String) -> Unit,
    textFieldColors: TextFieldColors,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = authState.registerUsernameInput,
            onValueChange = onRegisterUsernameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Username") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username Icon") },
            isError = authState.errorMessage != null,
            singleLine = true,
            colors = textFieldColors,
        )
        OutlinedTextField(
            value = authState.registerEmailInput,
            onValueChange = onRegisterEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
            isError = authState.errorMessage != null,
            singleLine = true,
            colors = textFieldColors,
        )
        PasswordTextField(
            modifier = Modifier.fillMaxWidth(),
            label = "Confirm Password", // Reused with a different label
            value = authState.registerPasswordInput,
            onValueChange = onRegisterPasswordChange,
            isError = authState.errorMessage != null,
            textFieldColors = textFieldColors
        )
    }
}