package crossBoard.ui

import androidx.compose.runtime.Composable
import crossBoard.domain.MatchState
import crossBoard.model.MatchUiState

@Composable
fun GameFlowScreen(
    matchUiState: MatchUiState,
    currentUserId: Int?,
    onCancelSearch: () -> Unit,
    onMakeMove: (row:Int, column:Int) -> Unit,
    onForfeit: () -> Unit,
    onResetMatch: () -> Unit,
) {
    val currentMatch = matchUiState.currentMatch
    if (currentMatch != null) {
        if (currentMatch.state == MatchState.WAITING)
            WaitingScreen(
                errorMessage = matchUiState.errorMessage,
                onCancelClick = onCancelSearch
            )
        else
            GameScreen(
                match = currentMatch,
                player1Username = matchUiState.player1Username,
                player2Username = matchUiState.player2Username,
                currentUserId = currentUserId,
                isLoading = matchUiState.isLoading,
                errorMessage = matchUiState.errorMessage,
                onCellClick = onMakeMove,
                onForfeitClick = onForfeit,
                onPlayAgainClick = onResetMatch
            )
    }
}