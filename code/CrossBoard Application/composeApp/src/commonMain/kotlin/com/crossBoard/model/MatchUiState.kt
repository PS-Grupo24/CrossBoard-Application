package com.crossBoard.model

import com.crossBoard.domain.MultiPlayerMatch

data class MatchUiState(
    val currentMatch: MultiPlayerMatch? = null,
    val player1Username: String = "",
    val player2Username: String = "",
    val gameTypeInput: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val webSocketErrorMessage: String? = null,
    val incomingWebSocketErrorMessage: List<String> = emptyList(),
    val timeLeftSeconds: Int? = null,
)