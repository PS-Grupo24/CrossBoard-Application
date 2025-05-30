package com.crossBoard.model

import androidx.compose.runtime.Immutable
import com.crossBoard.domain.UserInfo

@Immutable
data class AdminState(
    val searchQuery: String = "",
    val searchResults: List<UserInfo> = emptyList(),
    val isSearching: Boolean = false,
    val searchError: String? = null,

    val selectedUser: UserInfo? = null,

    val isModifyingUser: Boolean = false,
    val modifyUserError: String? = null,
    val modifyUserSuccess: String? = null
)