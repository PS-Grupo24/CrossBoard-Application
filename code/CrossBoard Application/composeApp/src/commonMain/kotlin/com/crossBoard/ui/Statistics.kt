package com.crossBoard.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.crossBoard.ApiClient
import com.crossBoard.domain.User
import com.crossBoard.ui.screens.StatisticsScreen
import com.crossBoard.ui.viewModel.StatsViewModel

/**
 * Activity for the statistics functionality.
 * It uses `StatsViewModel` to manage the statistics and `StatisticsScreen` to display the statistics.
 * @param user The logged user.
 * @param client The client to perform requests.
 */
@Composable
fun Statistics(
    user: User,
    client: ApiClient,
){
    val vm = remember { StatsViewModel(user.token.value, client) }
    DisposableEffect(Unit) {
        onDispose {
            vm.clear()
        }
    }
    vm.fetchStats()
    val statsState = vm.stats.collectAsState()
    StatisticsScreen(statsState.value)
}