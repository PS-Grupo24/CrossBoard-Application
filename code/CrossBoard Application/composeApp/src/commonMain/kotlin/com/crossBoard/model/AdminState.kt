package com.crossBoard.model

import androidx.compose.runtime.Immutable
import com.crossBoard.domain.UserInfo

/**
 * AdminState responsible for managing the resources used in the display.
 * @param searchQuery The username query to search for.
 * @param searchResults The user results from the search.
 * @param isSearching Flag keeping track if search is ongoing.
 * @param searchError The search error message; `NULL` for no error.
 * @param selectedUser The highlighted user.
 * @param isModifyingUser Flag keeping track if a user is being modified or not.
 * @param modifyUserError The error message when modifying a user; `NULL` for no error.
 * @param modifyUserSuccess The message description on success.
 */
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