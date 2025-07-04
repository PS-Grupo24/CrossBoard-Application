package com.crossBoard.ui.viewModel

import com.crossBoard.ApiClient
import com.crossBoard.domain.Admin
import com.crossBoard.domain.UserInfo
import com.crossBoard.domain.UserState
import com.crossBoard.httpModel.UserProfileOutput
import com.crossBoard.interfaces.Clearable
import com.crossBoard.model.AdminState
import com.crossBoard.util.Failure
import com.crossBoard.util.Success
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * viewModel responsible for implementing the admin panel functionalities.
 * @param client The `APIClient` responsible for performing server requests.
 * @param user The admin user.
 * @param mainDispatcher The coroutine Dispatcher; `Dispatcher.Main` by default.
 */
class AdminViewModel (
    val client: ApiClient,
    val user: Admin,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main
): Clearable {
    private val viewModelScope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val _adminState = MutableStateFlow(AdminState())
    val adminState: StateFlow<AdminState> = _adminState.asStateFlow()

    /**
     * Responsible for updating the username search query.
     * If the query has a length > 2, the search is automatically made.
     * @param query The new username query.
     */
    fun updateSearchQuery(query: String) {
        _adminState.update {
            it.copy(
                searchQuery = query,
                searchError = null
            )
        }
        if (query.length > 2) performSearch()
    }

    /**
     * Responsible for performing the user search.
     * It limits the maximum amount of users to search to 3.
     */
    fun performSearch() {
        val currentState = _adminState.value
        val query = currentState.searchQuery.trim()
        val adminToken = user.token.value

        if (query.isBlank()) {
            _adminState.update { it.copy(searchResults = emptyList(), searchError = "Please enter a search query.", selectedUser = null) }
            return
        }
        if (currentState.isSearching) {
            println("Search already in progress.")
            return
        }

        viewModelScope.launch {
            _adminState.update { it.copy(isSearching = true, searchError = null, searchResults = emptyList(), selectedUser = null) }
            try {
                when (val result = client.getUsersByName(adminToken,query, 0, 3)) {
                    is Success -> {
                        println(result)
                        _adminState.update { it.copy(searchResults = result.value, isSearching = false) }
                        println(_adminState.value.searchResults.map { it.username })
                    }
                    is Failure -> {
                        _adminState.update { it.copy(searchError = result.value, isSearching = false) }
                    }
                }
            } catch (e: Exception) {
                println("Search exception: ${e.message}")
                _adminState.update { it.copy(searchError = "Search failed: ${e.message ?: "Unknown error"}", isSearching = false) }
            }
        }
    }

    /**
     * Responsible for highlighting a user in the list of found users.
     * @param user The user to highlight.
     */
    fun selectUser(user: UserInfo?) {
        _adminState.update { it.copy(selectedUser = user, modifyUserError = null, modifyUserSuccess = null) }
    }

    /**
     * Responsible for banning the highlighted user.
     */
    fun banSelectedUser() {
        val currentState = _adminState.value
        val userToModify = currentState.selectedUser
        val adminToken = user.token.value

        if (userToModify == null || currentState.isModifyingUser) {
            println("Ban called without selected user or while busy.")
            return
        }

        if (userToModify.state == UserState.BANNED.name) return

        viewModelScope.launch {
            _adminState.update { it.copy(isModifyingUser = true, modifyUserError = null, modifyUserSuccess = null) } // Show loading

            try {
                when (val result = client.banUser(adminToken, userToModify.id)) {
                    is Success -> {
                        _adminState.update { state ->
                            state.copy(
                                isModifyingUser = false,
                                modifyUserSuccess = "User ${userToModify.username.value} banned successfully.",
                                selectedUser = result.value,
                                searchResults = state.searchResults.map { if (it.id == userToModify.id) result.value else it },
                            )
                        }
                    }
                    is Failure -> {
                        println("Ban failed for user ${userToModify.id}: ${result.value}")
                        _adminState.update { it.copy(isModifyingUser = false, modifyUserError = result.value) }
                    }
                }
            } catch (e: Exception) {
                println("Ban exception for user ${userToModify.id}: ${e.message}")
                _adminState.update { it.copy(isModifyingUser = false, modifyUserError = "Ban failed: ${e.message ?: "Unknown error"}") }
            }
        }
    }

    /**
     * Responsible for unbanning the highlighted user.
     */
    fun unbanSelectedUser() {
        val currentState = _adminState.value
        val userToModify = currentState.selectedUser
        val adminToken = user.token.value

        if (userToModify == null || currentState.isModifyingUser) {
            println("Unban called without selected user or while busy.")
            return
        }

        if (userToModify.state == UserState.NORMAL.name) return


        viewModelScope.launch {
            _adminState.update { it.copy(isModifyingUser = true, modifyUserError = null, modifyUserSuccess = null) }

            try {
                when (val result = client.unbanUser(adminToken, userToModify.id)) {
                    is Success -> {
                        println("Unban successful for user ${userToModify.id}.")
                        _adminState.update { state ->
                            state.copy(
                                isModifyingUser = false,
                                modifyUserSuccess = "User ${userToModify.username.value} unbanned successfully.",
                                selectedUser = result.value,
                                searchResults = state.searchResults.map { if (it.id == userToModify.id) result.value else it },
                            )
                        }
                    }
                    is Failure -> {
                        println("Unban failed for user ${userToModify.id}: ${result.value}")
                        _adminState.update { it.copy(isModifyingUser = false, modifyUserError = result.value) }
                    }
                }
            } catch (e: Exception) {
                println("Unban exception for user ${userToModify.id}: ${e.message}")
                _adminState.update { it.copy(isModifyingUser = false, modifyUserError = "Unban failed: ${e.message ?: "Unknown error"}") }
            }
        }
    }

    /**
     * Performs the viewModel cleanup by canceling the viewModel scope.
     */
    override fun clear() {
        viewModelScope.cancel()
    }

}