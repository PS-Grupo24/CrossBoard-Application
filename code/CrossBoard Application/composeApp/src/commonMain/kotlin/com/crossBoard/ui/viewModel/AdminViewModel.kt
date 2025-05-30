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

class AdminViewModel (
    val client: ApiClient,
    val user: Admin,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main
): Clearable {
    private val viewModelScope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val _adminState = MutableStateFlow(AdminState()) // Initial state
    val adminState: StateFlow<AdminState> = _adminState.asStateFlow()

    fun updateSearchQuery(query: String) {
        _adminState.update {
            it.copy(
                searchQuery = query,
                searchError = null
            )
        }
        if (query.length > 2) performSearch()
    }

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

    fun selectUser(user: UserInfo?) {
        _adminState.update { it.copy(selectedUser = user, modifyUserError = null, modifyUserSuccess = null) }
    }

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

    override fun clear() {
        viewModelScope.cancel()
    }

}