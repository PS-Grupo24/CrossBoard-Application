package com.crossBoard.model

import com.crossBoard.domain.MultiPlayerMatch

data class MultiplayerMatchUiState(
    val currentMatch: MultiPlayerMatch? = null,
    val player1Username: String = "",
    val player2Username: String = "",
    val gameTypeInput: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val webSocketMessage: String? = null,
    val timeLeftSeconds: Int? = null,
)