package com.crossBoard.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
@Composable
fun AdminPanel(
    user: Admin,
    client: ApiClient,
){
    val adminViewModel = remember { AdminViewModel(client, user) }
    val adminState by adminViewModel.adminState.collectAsState()

    AdminPanelScreen(
        adminState,
        adminViewModel
    )
}

