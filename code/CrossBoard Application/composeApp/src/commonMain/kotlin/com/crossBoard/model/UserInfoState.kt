package com.crossBoard.model

data class UserInfoState(
    val id: Int? = null,
    val username: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)