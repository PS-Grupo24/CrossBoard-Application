package crossBoard.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import crossBoard.viewModel.MatchUiState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoggedInContent(
    matchState: MatchUiState,
    onGameTypeChange: (String) -> Unit,
    onFindMatch: () -> Unit,
    onMakeMove: (row: Int, column: Int) -> Unit,
    onForfeit: () -> Unit,
    onResetMatch: () -> Unit,
    onLogout: () -> Unit,
    currentUserId: Int?,
){
    val matchExists = matchState.currentMatch != null

    AnimatedContent(targetState = matchExists, label = "ScreenTransition"){ isMatchDisplayed ->
        if (!isMatchDisplayed){
            FindMatchScreen(
                selectedGameTypeValue = matchState.gameTypeInput,
                isLoading = matchState.isLoading,
                errorMessage = matchState.errorMessage,
                onGameTypeChange = onGameTypeChange,
                onFindMatchClick = onFindMatch,
                )
        }
        else{
            val currentMatch = matchState.currentMatch
            if (currentMatch != null){
                GameScreen(
                    match = currentMatch,
                    player1Username = matchState.player1Username,
                    player2Username = matchState.player2Username,
                    isLoading = matchState.isLoading,
                    errorMessage = matchState.errorMessage,
                    currentUserId = currentUserId,
                    onCellClick = onMakeMove,
                    onForfeitClick = onForfeit,
                    onPlayAgainClick = onResetMatch,
                )
            }

        }
    }
}