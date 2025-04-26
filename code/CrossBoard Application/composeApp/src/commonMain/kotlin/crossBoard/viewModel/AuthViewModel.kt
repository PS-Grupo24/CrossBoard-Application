package crossBoard.viewModel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import crossBoard.ApiClient
import crossBoard.model.AuthState
import crossBoard.model.LoggedUser
import crossBoard.util.Failure
import crossBoard.util.Success

class AuthViewModel(
    private val client: ApiClient,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) : Clearable {

    private val viewModelScope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun updateLoginUsername(input: String) {
        _authState.update { it.copy(loginUsernameInput = input, errorMessage = null) }
    }

    fun updateLoginPassword(input: String) {
        _authState.update { it.copy(loginPasswordInput = input, errorMessage = null) }
    }

    fun updateRegisterUsername(input: String) {
        _authState.update { it.copy(registerUsernameInput = input, errorMessage = null) }
    }

    fun updateRegisterEmail(input: String) {
        _authState.update { it.copy(registerEmailInput = input, errorMessage = null) }
    }
    fun updateRegisterPassword(input: String) {
        _authState.update { it.copy(registerPasswordInput = input, errorMessage = null) }
    }

    fun showLoginScreen(show: Boolean) {
        _authState.update {
            it.copy(
                isLoginScreenVisible = show,
                errorMessage = null,
            )
        }
    }
    fun login(){
        val currentState = _authState.value
        if (currentState.isLoading) return

        if (currentState.loginUsernameInput.isBlank() || currentState.loginPasswordInput.isBlank()) {
            _authState.update { it.copy(errorMessage = "Username and Password cannot be empty") }
            return
        }

        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true, errorMessage = null) }
            when(val result = client.login(
                currentState.loginUsernameInput.trim(),
                currentState.loginPasswordInput,
            )){
                is Success -> {
                    _authState.update { it.copy(
                        isLoading = false,
                        userToken = result.value.token,
                        currentUser = LoggedUser(result.value.id, result.value.token),
                        loginPasswordInput = "",
                        loginUsernameInput = ""
                    ) }
                }
                is Failure -> {
                    _authState.update {
                        it.copy(isLoading = false, errorMessage = result.value)
                    }
                }
            }
        }
    }

    fun register() {
        val currentState = _authState.value
        if (currentState.isLoading) return

        if (
            currentState.registerUsernameInput.isBlank()
            || currentState.registerEmailInput.isBlank()
            || currentState.registerPasswordInput.isBlank()
            ){
            _authState.update { it.copy(errorMessage = "All fields are required.") }
            return
        }

        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true, errorMessage = null) }
            when(val result = client.register(
                currentState.registerUsernameInput.trim(),
                currentState.registerEmailInput.trim(),
                currentState.registerPasswordInput,
            )){
                is Success -> {
                    _authState.update { it.copy(
                        isLoading = false,
                        userToken = result.value.token,
                        currentUser = LoggedUser(result.value.id, result.value.token),
                        loginUsernameInput = currentState.loginUsernameInput,

                        registerEmailInput = "",
                        registerPasswordInput = "",
                        registerUsernameInput = ""
                    ) }
                }
                is Failure -> {
                    _authState.update { it.copy(isLoading = false, errorMessage = result.value) }
                }
            }
        }
    }

    fun logout(){
        _authState.value = AuthState()
    }

    override fun clear() {
        viewModelScope.cancel()
    }
}