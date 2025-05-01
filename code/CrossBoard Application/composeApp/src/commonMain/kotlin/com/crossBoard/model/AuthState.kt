package com.crossBoard.model

import androidx.compose.runtime.Immutable

@Immutable
data class AuthState(
    val userToken: String? = null,
    val currentUser: LoggedUser? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginScreenVisible: Boolean = true,
    val maintainSession: Boolean = false,

    val loginUsernameInput: String = "",
    val loginPasswordInput: String = "",
    val registerUsernameInput: String = "",
    val registerEmailInput: String = "",
    val registerPasswordInput: String = ""
) {
    val isAuthenticated: Boolean get() = userToken != null
}