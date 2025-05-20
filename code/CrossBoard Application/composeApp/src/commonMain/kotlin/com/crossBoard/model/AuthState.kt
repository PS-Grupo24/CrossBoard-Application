package com.crossBoard.model

import androidx.compose.runtime.Immutable
import com.crossBoard.domain.User

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