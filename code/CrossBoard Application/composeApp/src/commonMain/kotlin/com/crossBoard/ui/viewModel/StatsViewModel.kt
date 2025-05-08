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

class StatsViewModel(
    private val userToken: String,
    private val client: ApiClient,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main
): Clearable {
    private val viewModelScope = CoroutineScope(SupervisorJob() + mainDispatcher)

    private val _stats = MutableStateFlow(StatsState())
    val stats: StateFlow<StatsState> = _stats.asStateFlow()

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

    override fun clear() {
        _stats.value = StatsState()
        viewModelScope.cancel()
    }
}