package com.crossBoard.ui.viewModel

import com.crossBoard.ApiClient
import com.crossBoard.model.UserInfoState
import com.crossBoard.util.Failure
import com.crossBoard.util.Success
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class UserInfoViewModel(
    private val client: ApiClient,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
): Clearable {
    private val viewModelScope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val _user = MutableStateFlow<UserInfoState>(UserInfoState())
    val user = _user

    fun getUser(userId: Int){
        viewModelScope.launch {
            try {
                when(val response = client.getUserById(userId)){
                    is Success -> {
                        _user.update { it.copy(id = response.value.id, email = response.value.email, username = response.value.username)  }
                    }
                    is Failure -> {
                        _user.update { it.copy(isLoading = false, errorMessage = response.value) }
                    }
                }
            }
            catch (e: Exception){
                _user.update {it.copy(isLoading = false, errorMessage = e.message ?: e.cause?.message) }
            }

        }
    }

    override fun clear() {
        viewModelScope.cancel()
    }
}