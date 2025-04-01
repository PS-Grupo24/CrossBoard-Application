package org.example.project

import androidx.compose.runtime.Composable

@Composable
fun FindMatchScreen(
    userId: String,
    onUserIdChange: (String) -> Unit,
    gameType: String,
    onGameTypeChange: (String) -> Unit,
    onFindMatchClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
){

}