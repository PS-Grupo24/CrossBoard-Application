package com.crossBoard.model

import com.crossBoard.domain.MultiPlayerMatch

/**
 * MultiplayerMatchUiState responsible for tracking the resources inside the multiplayer match functionality.
 * @param currentMatch The current match; `NULL` when the user is not in a match.
 * @param player1Username The username of player1 to be displayed.
 * @param player2Username The username of player2 to be displayed.
 * @param gameTypeInput The match type to be searched for.
 * @param isLoading Flag tracking if a request is currently loading.
 * @param errorMessage The error message found; `NULL` when no error found.
 * @param timeLeftSeconds The time left in the current match turn.
 */
data class MultiplayerMatchUiState(
    val currentMatch: MultiPlayerMatch? = null,
    val player1Username: String = "",
    val player2Username: String = "",
    val gameTypeInput: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val timeLeftSeconds: Int? = null,
)