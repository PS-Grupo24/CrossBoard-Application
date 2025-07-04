package com.crossBoard.ui.screens

import androidx.compose.runtime.Composable
import com.crossBoard.domain.MatchState
import com.crossBoard.model.MultiplayerMatchUiState

/**
 * Screen responsible for displaying the different states of a match.
 * Calls for `WaitingScreen` when the current match is still waiting for an opponent.
 * Calls for `GameScreem when the current match started.
 * @param multiplayerMatchUiState The current state of the match.
 * @param currentUserId The id of the current logged user.
 * @param onCancelSearch The action to perform when the match is canceled.
 * @param onMakeMove The action to perform when making a move.
 * @param onForfeit The action to perform when forfeiting.
 * @param onResetMatch The action to perform when cleaning up the match.
 */
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
                onCellClick = onMakeMove,
                onForfeitClick = onForfeit,
                onPlayAgainClick = onResetMatch,
                timeLeft = multiplayerMatchUiState.timeLeftSeconds
            )
    }
}