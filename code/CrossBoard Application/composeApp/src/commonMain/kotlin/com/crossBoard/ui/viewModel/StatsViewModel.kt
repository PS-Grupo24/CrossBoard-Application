package com.crossBoard.ui.viewModel

import com.crossBoard.ApiClient
import com.crossBoard.interfaces.Clearable
import com.crossBoard.model.StatsState
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
 * ViewModel `StatsViewModel` responsible for managing the state of the match Statistics using `StatsState`.
 * It is responsible for calling the `getMatchStatistics` request from `APIClient`.
 * @param userToken The token of the logged user.
 * @param client The `APIClient` responsible for performing requests to the server.
 * @param mainDispatcher The coroutine dispatcher; `Dispatchers.Main` by default.
 */
class StatsViewModel(
    private val userToken: String,
    private val client: ApiClient,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main
): Clearable {
    /**
     * The scope for this viewModel
     */
    private val viewModelScope = CoroutineScope(SupervisorJob() + mainDispatcher)

    /**
     * The mutable state of flow to manage the `StatsState`.
     */
    private val _stats = MutableStateFlow(StatsState())

    /**
     * The StateFlow to collect from.
     */
    val stats: StateFlow<StatsState> = _stats.asStateFlow()

    /**
     * Function "fetchStats" responsible for performing a request to `getMatchStatistics` request from `APIClient`
     * and updating the `StatsState` with the obtained result.
     */
    fun fetchStats() {
        _stats.update{ it.copy(errorMessage = null) }

        viewModelScope.launch {
            when(val result = client.getMatchStatistics(userToken)){
                is Success -> {
                    _stats.update{ it.copy(stats = result.value,errorMessage = null)}
                }
                is Failure -> {
                    _stats.update{ it.copy(errorMessage = result.value)}
                }
            }
        }
    }

    /**
     * Function "clear" that performs the cleanup of this viewModel
     * It clears the StatsState and cancels the viewModel scope.
     */
    override fun clear() {
        _stats.value = StatsState()
        viewModelScope.cancel()
    }
}