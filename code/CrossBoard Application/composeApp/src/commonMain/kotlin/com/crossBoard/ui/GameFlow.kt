package com.crossBoard.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import com.crossBoard.ApiClient
import com.crossBoard.ui.screens.FindMatchScreen
import com.crossBoard.ui.screens.GameFlowScreen
import com.crossBoard.ui.viewModel.MultiplayerMatchViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GameFlow(
    client: ApiClient,
    userToken: String,
    currentUserId: Int,
    onFindMatch: () -> Unit,
    onMatch: () -> Unit,
){
    val vm = remember { MultiplayerMatchViewModel(client, userToken, currentUserId) }
    val matchUiState by vm.matchState.collectAsState()
    DisposableEffect(Unit){
        onDispose {
            vm.clear()
        }
    }
    val currentMatch = matchUiState.currentMatch
    if (currentMatch == null){
        onFindMatch()
        FindMatchScreen(
            selectedGameTypeValue = matchUiState.gameTypeInput,
            isLoading = matchUiState.isLoading,
            errorMessage = matchUiState.errorMessage,
            onGameTypeChange = vm::updateGameTypeInput,
            onFindMatchClick = vm::findMatch,
        )
    }
    else{
        onMatch()
        GameFlowScreen(
            multiplayerMatchUiState = matchUiState,
            currentUserId = currentUserId,
            onCancelSearch = vm::cancelSearch,
            onMakeMove = { row, column -> vm.makeMove(row, column) },
            onForfeit = vm::forfeit,
            onResetMatch = vm::resetMatch,
        )
    }

}