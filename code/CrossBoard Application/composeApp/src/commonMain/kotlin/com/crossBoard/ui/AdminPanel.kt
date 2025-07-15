package com.crossBoard.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import com.crossBoard.ApiClient
import com.crossBoard.domain.Admin
import com.crossBoard.ui.screens.AdminPanelScreen
import com.crossBoard.ui.viewModel.AdminViewModel

/**
 * Activity for the AdminPanel functionality.
 * Uses `AdminViewModel` and `AdminPanelScreen`.
 * @param user The logged admin.
 * @param client The client to perform requests
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AdminPanel(
    user: Admin,
    client: ApiClient,
    onBack: () -> Unit,
){
    val adminViewModel = remember { AdminViewModel(client, user) }
    val adminState by adminViewModel.adminState.collectAsState()
    BackHandler(onBack = onBack)
    AdminPanelScreen(
        adminState,
        adminViewModel
    )
}

