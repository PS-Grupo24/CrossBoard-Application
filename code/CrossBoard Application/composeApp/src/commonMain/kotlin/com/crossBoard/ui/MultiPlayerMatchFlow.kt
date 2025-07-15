package com.crossBoard.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import com.crossBoard.ApiClient
import com.crossBoard.ui.screens.FindMatchScreen
import com.crossBoard.ui.screens.GameFlowScreen
import com.crossBoard.ui.viewModel.MultiplayerMatchViewModel

/**
 * Activity responsible for the MultiPlayerMatch functionality.
 * Uses `MultiPlayerMatchViewModel` to manage the match resources and actions.
 * Uses `FindMatchScreen` initially when there is no match and `GameFlowScreen when a match is found.
 * @param client The client to perform requests.
 * @param userToken The token of the logged user.
 * @param currentUserId The id of the logged user.
 * @param onFindMatch Action to perform when FindMatchScreen is to be called.
 * @param onMatch Action to perform when a Match is found.
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun MultiPlayerMatchFlow(
    client: ApiClient,
    userToken: String,
    currentUserId: Int,
    onFindMatch: () -> Unit,
    onMatch: () -> Unit,
    onMatchOver: () -> Unit,
    onBack: () -> Unit,
){
    val vm = remember { MultiplayerMatchViewModel(client, userToken, currentUserId) }
    val matchUiState by vm.matchState.collectAsState()
    DisposableEffect(Unit){
        onDispose {
            vm.clear()
        }
    }
    BackHandler(onBack = onBack)
    val currentMatch = matchUiState.currentMatch
    if (currentMatch == null){
        onFindMatch()
        FindMatchScreen(
            selectedGameTypeValue = matchUiState.gameTypeInput,
            isLoading = matchUiState.isLoading,
            errorMessage = matchUiState.errorMessage,
            onGameTypeChange = vm::updateMatchTypeInput,
            onFindMatchClick = vm::findMatch,
        )
    }
    else{
        onMatch()
        GameFlowScreen(
            multiplayerMatchUiState = matchUiState,
            currentUserId = currentUserId,
            onCancelSearch = vm::cancelSearch,
            onMakeMove = { move -> vm.makeMove(move) },
            onForfeit = vm::forfeit,
            onPlayAgain = vm::findMatch,
            onMatchOver = onMatchOver,
            onBack = vm::resetMatch
        )
    }

}