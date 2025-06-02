package com.crossBoard.ui.screens

import androidx.compose.runtime.Composable
import com.crossBoard.domain.MatchState
import com.crossBoard.model.MultiplayerMatchUiState

@Composable
fun GameFlowScreen(
    multiplayerMatchUiState: MultiplayerMatchUiState,
    currentUserId: Int?,
    onCancelSearch: () -> Unit,
    onMakeMove: (row:Int, column:Int) -> Unit,
    onForfeit: () -> Unit,
    onResetMatch: () -> Unit,
) {
    val currentMatch = multiplayerMatchUiState.currentMatch
    if (currentMatch != null) {
        if (currentMatch.state == MatchState.WAITING)
            WaitingScreen(
                message = "Looking for a ${currentMatch.matchType.name} Match",
                errorMessage = multiplayerMatchUiState.errorMessage,
                onCancelClick = onCancelSearch
            )
        else
            GameScreen(
                match = currentMatch,
                player1Username = multiplayerMatchUiState.player1Username,
                player2Username = multiplayerMatchUiState.player2Username,
                currentUserId = currentUserId,
                isLoading = multiplayerMatchUiState.isLoading,
                errorMessage = multiplayerMatchUiState.errorMessage,
                webSocketMessage = multiplayerMatchUiState.webSocketMessage,
                onCellClick = onMakeMove,
                onForfeitClick = onForfeit,
                onPlayAgainClick = onResetMatch,
                timeLeft = multiplayerMatchUiState.timeLeftSeconds
            )
    }
}