package com.crossBoard.ui.viewModel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.crossBoard.ApiClient
import com.crossBoard.domain.*
import com.crossBoard.interfaces.Clearable
import com.crossBoard.model.AuthState
import com.crossBoard.util.Failure
import com.crossBoard.util.Success
import com.russhwolf.settings.Settings

class AuthViewModel(
    private val client: ApiClient,
    private val settings: Settings,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) : Clearable {

    private val viewModelScope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val _authState = MutableStateFlow(AuthState())
    private val tokenSettingsString = "userToken"
    private val idSettingsString = "userID"
    private val nameSettingsString = "userUsername"
    private val emailSettingsString = "userEmail"
    private val passwordSettingsString = "userPassword"
    private val stateSettingsString = "userState"
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

    fun playMatch(value: Boolean){
        _authState.update {
            it.copy(isLoading = false, errorMessage = null, playMatch = value)
        }
    }

    fun login(){
        try {
            val currentState = _authState.value
            if (currentState.isLoading) return

            val username = Username(currentState.loginUsernameInput.trim())
            val password = Password(currentState.loginPasswordInput)

            viewModelScope.launch {
                _authState.update { it.copy(isLoading = true, errorMessage = null) }
                when(val result = client.login(
                    username.value, password.value
                )){
                    is Success -> {
                        val user = if (result.value.state == Admin.STATE)
                            Admin(
                                result.value.id,
                                username,
                                Email(result.value.email),
                                password,
                                Token(result.value.token),
                            )
                        else NormalUser(
                                result.value.id,
                                username,
                                Email(result.value.email),
                                password,
                                Token(result.value.token),
                                UserState.valueOf(result.value.state)
                            )
                        _authState.update { it.copy(
                            isLoading = false,
                            user = user,
                            loginPasswordInput = "",
                            loginUsernameInput = ""
                        ) }
                        if (_authState.value.maintainSession) storeSettings(
                            settings,
                            result.value.id,
                            username.value,
                            result.value.email,
                            password.value,
                            result.value.token,
                            result.value.state,
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
        catch (e: Exception){
            _authState.update { it.copy(isLoading = false, errorMessage = e.message ?: e.cause?.message) }
        }
    }

    fun register() {
        try {
            val currentState = _authState.value
            if (currentState.isLoading) return

            val username = Username(currentState.registerUsernameInput.trim())
            val email = Email(currentState.registerEmailInput.trim())
            val password = Password(currentState.registerPasswordInput)

            viewModelScope.launch {
                _authState.update { it.copy(isLoading = true, errorMessage = null) }
                when(val result = client.register(
                    username.value, email.value, password.value
                )){
                    is Success -> {
                        val state = UserState.NORMAL
                        _authState.update { it.copy(
                            isLoading = false,
                            user = NormalUser(
                                result.value.id,
                                username,
                                email,
                                password,
                                Token(result.value.token),
                                state
                            ),
                            loginUsernameInput = currentState.loginUsernameInput,

                            registerEmailInput = "",
                            registerPasswordInput = "",
                            registerUsernameInput = ""
                        ) }
                        if (_authState.value.maintainSession) storeSettings(
                            settings,
                            result.value.id,
                            username.value,
                            email.value,
                            password.value,
                            result.value.token,
                            state.name,
                        )
                    }
                    is Failure -> {
                        _authState.update { it.copy(isLoading = false, errorMessage = result.value) }
                    }
                }
            }
        }
        catch (e: Exception){
            _authState.update { it.copy(isLoading = false, errorMessage = e.message ?: e.cause?.message) }
        }
    }

    fun logout(){
        _authState.value = AuthState()
        settings.remove(tokenSettingsString)
        settings.remove(idSettingsString)
        settings.remove(nameSettingsString)
        settings.remove(emailSettingsString)
        settings.remove(passwordSettingsString)
        settings.remove(stateSettingsString)
    }

    fun checkSession(){
        val token = settings.getStringOrNull(tokenSettingsString)
        val id = settings.getIntOrNull(idSettingsString)
        val email = settings.getStringOrNull(emailSettingsString)
        val password = settings.getStringOrNull(passwordSettingsString)
        val username = settings.getStringOrNull(nameSettingsString)
        val state = settings.getStringOrNull(stateSettingsString)
        if (token != null && id != null && email != null && password != null && username != null && state != null) {
            val user = if (state == Admin.STATE)
                Admin(id, Username(username), Email(email), Password(password), Token(token))

            else NormalUser(id, Username(username), Email(email), Password(password), Token(token), UserState.valueOf(state))
            _authState.update{ it.copy(user = user,isLoading = false, errorMessage = null) }
        }
    }

    override fun clear() {
        viewModelScope.cancel()
    }

    private fun storeSettings(settings: Settings, id: Int, username: String, email: String, password: String, token: String, state: String) {
        settings.putInt(idSettingsString, id)
        settings.putString(nameSettingsString, username)
        settings.putString(passwordSettingsString, password)
        settings.putString(tokenSettingsString, token)
        settings.putString(emailSettingsString, email)
        settings.putString(stateSettingsString, state)
    }


}