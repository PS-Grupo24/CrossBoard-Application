package org.example.project.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.*
import org.example.project.MatchClient
import org.example.project.TicTacToeViewModel

@Composable
fun ticTacToeApp(client: MatchClient){
    val appState = remember { TicTacToeViewModel(client) }

    DisposableEffect(Unit) {
        onDispose {
            appState.clear()
        }
    }

    val uiState by appState.uiState.collectAsState()
    val matchExists = uiState.currentMatch != null

    AnimatedContent(targetState = matchExists, label = "ScreenTransition"){ isMatchDisplayed ->
        if (!isMatchDisplayed){
            FindMatchScreen(
                uiState.userIdInput,
                gameType = uiState.gameTypeInput,
                isLoading = uiState.isLoading,
                errorMessage = uiState.errorMessage,

                onUserIdChange = appState::updateUserIdInput,
                onGameTypeChange = appState::updateGameTypeInput,
                onFindMatchClick = appState::findMatch,


            )
        }
        else{
            val currentMatch = uiState.currentMatch
            if (currentMatch != null){
                GameScreen(
                    match = currentMatch,
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                    currentUserId = uiState.currentUserId ?: 1,
                    onCellClick = appState::makeMove,
                    onForfeitClick = appState::forfeit,
                    onPlayAgainClick = appState::resetMatch,
                )
            }

        }
    }
}