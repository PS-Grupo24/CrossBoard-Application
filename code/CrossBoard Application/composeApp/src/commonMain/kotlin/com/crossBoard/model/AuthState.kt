package com.crossBoard.model

import androidx.compose.runtime.Immutable
import com.crossBoard.domain.User

/**
 * AuthState responsible for tracking the resources used in authentication.
 * @param user The current logged user; `NULL` on start.
 * @param isLoading Flag tracking if a request is loading.
 * @param errorMessage The error message on authentication; `NULL` when found no error.
 * @param isLoginScreenVisible Flag tracking which screen to show. `True` for `LoginScreen`; `False` for `RegisterScreen`
 * @param maintainSession Flag tracking if the user intends to save the session data.
 * @param playMatch Flag tracking if the user desires to play a match.
 * @param loginPasswordInput The password input for the login.
 * @param loginUsernameInput The username input for the login.
 * @param registerUsernameInput The username input for registration.
 * @param registerEmailInput The email input for registration.
 * @param registerPasswordInput The password input for registration.
 * @see isAuthenticated Property indicating if the user is authenticated.
 */
@Immutable
data class AuthState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginScreenVisible: Boolean = true,
    val maintainSession: Boolean = false,
    val playMatch: Boolean = false,

    val loginUsernameInput: String = "",
    val loginPasswordInput: String = "",
    val registerUsernameInput: String = "",
    val registerEmailInput: String = "",
    val registerPasswordInput: String = ""
) {
    val isAuthenticated: Boolean get() = user != null
}