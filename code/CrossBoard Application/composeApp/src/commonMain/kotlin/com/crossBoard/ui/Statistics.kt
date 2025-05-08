package com.crossBoard.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.crossBoard.ApiClient
import com.crossBoard.domain.User
import com.crossBoard.ui.screens.StatisticsScreen
import com.crossBoard.ui.viewModel.StatsViewModel

@Composable
fun Statistics(
    user: User,
    client: ApiClient,
){
    val vm = remember { StatsViewModel(user.token.value, client) }
    vm.fetchStats()
    val statsState = vm.stats.collectAsState()
    StatisticsScreen(statsState.value)
}