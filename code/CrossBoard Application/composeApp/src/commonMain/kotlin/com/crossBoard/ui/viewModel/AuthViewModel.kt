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
import com.crossBoard.util.hashPassword
import com.russhwolf.settings.Settings

/**
 * viewModel "AuthViewModel" responsible for managing the authentication or registration.
 * @param client The `APIClient` responsible for making server requests.
 * @param settings The `Settings` where the session data is stored.
 * @param mainDispatcher The coroutine dispatcher; `Dispatcher.Main` by default.
 */
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

    /**
     * Responsible for updating the username value for login.
     * @param input The username input.
     */
    fun updateLoginUsername(input: String) {
        _authState.update { it.copy(loginUsernameInput = input, errorMessage = null) }
    }
    /**
     * Responsible for updating the password value for login.
     * @param input The password input.
     */
    fun updateLoginPassword(input: String) {
        _authState.update { it.copy(loginPasswordInput = input, errorMessage = null) }
    }
    /**
     * Responsible for updating the username value for registration.
     * @param input The username input.
     */
    fun updateRegisterUsername(input: String) {
        _authState.update { it.copy(registerUsernameInput = input, errorMessage = null) }
    }
    /**
     * Responsible for updating the email value for registration.
     * @param input The email input.
     */
    fun updateRegisterEmail(input: String) {
        _authState.update { it.copy(registerEmailInput = input, errorMessage = null) }
    }
    /**
     * Responsible for updating the password value for registration.
     * @param input The password input.
     */
    fun updateRegisterPassword(input: String) {
        _authState.update { it.copy(registerPasswordInput = input, errorMessage = null) }
    }

    /**
     * Responsible for indicating if the user wants to maintain the session.
     * @param maintain `Boolean` value; `True` if maintainSession is wanted; `False` otherwise.
     */
    fun maintainSession(maintain: Boolean) {
        _authState.update {
            it.copy(
                maintainSession = maintain
            )
        }
    }

    /**
     * Responsible for switching between the login or registration screens.
     * @param show `Boolean` value; `True` to display the login screen; `False` to display the registration screen.
     */
    fun showLoginScreen(show: Boolean) {
        _authState.update {
            it.copy(
                maintainSession = false,
                isLoginScreenVisible = show,
                errorMessage = null,
            )
        }
    }

    /**
     * Responsible for indicating if a singleplayer match is wanted.
     * @param value `Boolean` value; `True` if match is wanted; `False` otherwise.
     */
    fun playMatch(value: Boolean){
        _authState.update {
            it.copy(isLoading = false, errorMessage = null, playMatch = value)
        }
    }

    /**
     * Responsible for performing the login with the field inputs.
     * It validates the username and password values by encapsulating them in the `Domain` entities `Username`
     * and `Password`.
     */
    fun login(){
        try {
            val currentState = _authState.value
            if (currentState.isLoading) return

            val username = Username(currentState.loginUsernameInput.trim())
            val password = Password(currentState.loginPasswordInput)
            val passwordHash = hashPassword(password.value)
            viewModelScope.launch {
                _authState.update { it.copy(isLoading = true, errorMessage = null) }
                when(val result = client.login(
                    username.value, passwordHash
                )){
                    is Success -> {
                        val user = if (result.value.state == Admin.STATE)
                            Admin(
                                result.value.id,
                                username,
                                result.value.email,
                                passwordHash,
                                result.value.token,
                            )
                        else NormalUser(
                                result.value.id,
                                username,
                                result.value.email,
                                passwordHash,
                                result.value.token,
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
                            result.value.email.value,
                            passwordHash,
                            result.value.token.value,
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
    /**
     * Responsible for performing the registration with the field inputs.
     * It validates the username, password and email values by encapsulating them in the `Domain` entities `Username`,
     * `Password` and `Email`.
     */
    fun register() {
        try {
            val currentState = _authState.value
            if (currentState.isLoading) return

            val username = Username(currentState.registerUsernameInput.trim())
            val email = Email(currentState.registerEmailInput.trim())
            val password = Password(currentState.registerPasswordInput)
            val passwordHash = hashPassword(password.value)
            viewModelScope.launch {
                _authState.update { it.copy(isLoading = true, errorMessage = null) }
                when(val result = client.register(
                    username.value, email.value, passwordHash
                )){
                    is Success -> {
                        val state = result.value.state
                        _authState.update { it.copy(
                            isLoading = false,
                            user = NormalUser(
                                result.value.id,
                                username,
                                email,
                                passwordHash,
                                result.value.token,
                                UserState.valueOf(state)
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
                            passwordHash,
                            result.value.token.value,
                            state,
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

    /**
     * Performs the logout by clearing `AuthState` and removes the session data from `Settings`.
     */
    fun logout(){
        _authState.value = AuthState()
        settings.remove(tokenSettingsString)
        settings.remove(idSettingsString)
        settings.remove(nameSettingsString)
        settings.remove(emailSettingsString)
        settings.remove(passwordSettingsString)
        settings.remove(stateSettingsString)
    }

    /**
     * Responsible for checking if there is already session stored in `Settings` before the authentication begins.
     */
    fun checkSession(){
        val token = settings.getStringOrNull(tokenSettingsString)
        val id = settings.getIntOrNull(idSettingsString)
        val email = settings.getStringOrNull(emailSettingsString)
        val password = settings.getStringOrNull(passwordSettingsString)
        val username = settings.getStringOrNull(nameSettingsString)
        val state = settings.getStringOrNull(stateSettingsString)
        if (token != null && id != null && email != null && password != null && username != null && state != null) {
            val user = if (state == Admin.STATE)
                Admin(id, Username(username), Email(email), password, Token(token))

            else NormalUser(id, Username(username), Email(email), password, Token(token), UserState.valueOf(state))
            _authState.update{ it.copy(user = user,isLoading = false, errorMessage = null) }
        }
    }

    /**
     * Performs the viewModel cleanup.
     * It cancels the viewModel scope.
     */
    override fun clear() {
        viewModelScope.cancel()
    }

    /**
     * Responsible for saving the user information to maintain a session.
     * @param settings The `Settings` so save the data at.
     * @param id The user id.
     * @param username The user username.
     * @param email The user email.
     * @param password The user password.
     * @param token The user token.
     * @param state The user state.
     */
    private fun storeSettings(settings: Settings, id: Int, username: String, email: String, password: String, token: String, state: String) {
        settings.putInt(idSettingsString, id)
        settings.putString(nameSettingsString, username)
        settings.putString(passwordSettingsString, password)
        settings.putString(tokenSettingsString, token)
        settings.putString(emailSettingsString, email)
        settings.putString(stateSettingsString, state)
    }


}