package org.example.project.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import org.example.project.MatchClient
import org.example.project.TicTacToeViewModel

@Composable
fun ticTacToeApp(client: MatchClient){
    val scope = rememberCoroutineScope()

    val appState = remember { TicTacToeViewModel(scope, client) }

    AnimatedContent(targetState = appState.currentMatch){ match ->
        if (match == null){
            FindMatchScreen(
                appState.userIdInput,
                onUserIdChange = {appState.userIdInput = it},
                gameType = appState.gameTypeInput,
                onGameTypeChange = {appState.gameTypeInput = it},
                onFindMatchClick = appState::findMatch,
                isLoading = appState.isLoading,
                errorMessage = appState.errorMessage
            )
        }
        else{
            GameScreen(
                match = match,
                currentUserId = appState.currentUserId,
                onCellClick = appState::makeMove,
                onForfeitClick = appState::forfeit,
                isLoading = appState.isLoading,
                errorMessage = appState.errorMessage,
                onPlayAgainClick = {appState.currentMatch = null },
            )
        }
    }
}