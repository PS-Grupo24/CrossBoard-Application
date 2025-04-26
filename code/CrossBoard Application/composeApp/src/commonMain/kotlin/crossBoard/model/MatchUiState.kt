package crossBoard.model

import crossBoard.domain.MultiPlayerMatch

data class MatchUiState(
    val currentMatch: MultiPlayerMatch? = null,
    val player1Username: String = "",
    val player2Username: String = "",
    val gameTypeInput: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)