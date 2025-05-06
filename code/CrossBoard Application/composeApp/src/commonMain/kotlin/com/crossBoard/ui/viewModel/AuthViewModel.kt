package com.crossBoard.ui.viewModel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.crossBoard.ApiClient
import com.crossBoard.domain.*
import com.crossBoard.model.AuthState
import com.crossBoard.util.Failure
import com.crossBoard.util.Success
import com.russhwolf.settings.Settings

class AuthViewModel(
    private val client: ApiClient,
    private val settings: Settings,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) : Clearable{

    private val viewModelScope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val _authState = MutableStateFlow(AuthState())
    private val tokenSettingsString = "userToken"
    private val idSettingsString = "userID"
    private val nameSettingsString = "userUsername"
    private val emailSettingsString = "userEmail"
    private val passwordSettingsString = "userPassword"
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

    fun maintainSession(maintain: Boolean) {
        _authState.update {
            it.copy(
                maintainSession = maintain
            )
        }
    }

    fun showLoginScreen(show: Boolean) {
        _authState.update {
            it.copy(
                maintainSession = false,
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
            val username = currentState.loginUsernameInput.trim()
            val password = currentState.loginPasswordInput
            when(val result = client.login(
                username, password
            )){
                is Success -> {
                    _authState.update { it.copy(
                        isLoading = false,
                        user = User(
                            result.value.id,
                            Username(username),
                            Email(result.value.email),
                            Password(password),
                            Token(result.value.token)
                        ),
                        loginPasswordInput = "",
                        loginUsernameInput = ""
                    ) }
                    if (_authState.value.maintainSession) storeSettings(
                        settings,
                        result.value.id,
                        username,
                        result.value.email,
                        password,
                        result.value.token
                    )
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
            val username = currentState.loginUsernameInput.trim()
            val email = currentState.registerEmailInput.trim()
            val password = currentState.registerPasswordInput
            when(val result = client.register(
                username, email, password
            )){
                is Success -> {
                    _authState.update { it.copy(
                        isLoading = false,
                        user = User(
                            result.value.id,
                            Username(username),
                            Email(email),
                            Password(password),
                            Token(result.value.token)

                        ),
                        loginUsernameInput = currentState.loginUsernameInput,

                        registerEmailInput = "",
                        registerPasswordInput = "",
                        registerUsernameInput = ""
                    ) }
                    if (_authState.value.maintainSession) storeSettings(
                        settings,
                        result.value.id,
                        username,
                        email,
                        password,
                        result.value.token
                    )
                }
                is Failure -> {
                    _authState.update { it.copy(isLoading = false, errorMessage = result.value) }
                }
            }
        }
    }

    fun logout(){
        _authState.value = AuthState()
        settings.remove(tokenSettingsString)
        settings.remove(idSettingsString)
        settings.remove(nameSettingsString)
        settings.remove(emailSettingsString)
        settings.remove(passwordSettingsString)
    }

    fun checkSession(){
        val token = settings.getStringOrNull(tokenSettingsString)
        val id = settings.getIntOrNull(idSettingsString)
        val email = settings.getStringOrNull(emailSettingsString)
        val password = settings.getStringOrNull(passwordSettingsString)
        val username = settings.getStringOrNull(nameSettingsString)
        if (token != null && id != null && email != null && password != null && username != null) {
            _authState.update{ it.copy(user = User(id, Username(username), Email(email), Password(password), Token(token)),isLoading = false, errorMessage = null) }
        }
    }

    override fun clear() {
        viewModelScope.cancel()
    }

    private fun storeSettings(settings: Settings, id: Int, username: String, email: String, password: String, token: String) {
        settings.putInt(idSettingsString, id)
        settings.putString(nameSettingsString, username)
        settings.putString(passwordSettingsString, password)
        settings.putString(tokenSettingsString, token)
        settings.putString(emailSettingsString, email)
    }


}