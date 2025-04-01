package org.example.project

import androidx.compose.runtime.Composable
import httpModel.MatchOutput

@Composable
fun GameScreen(
    match: MatchOutput,
    currentUserId: Int?,
    onCellClick: (row: Int, col: Int) -> Unit,
    onForfeitClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onPlayAgainClick: () -> Unit
){

}