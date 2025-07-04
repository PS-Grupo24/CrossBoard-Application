package com.crossBoard.model

import com.crossBoard.domain.Player

/**
 * SinglePlayerState responsible for saving the single player functionality resources.
 * @param match The current single player match; `NULL` when not in a match.
 * @param player The user's player type.
 * @param matchTypeInput The type of match to play.
 * @param errorMessage The error message to be displayed; `NULL` when no error found.
 */
data class SinglePlayerState(
    val match: SinglePlayerMatch? = null,
    val player: Player? = null,
    val matchTypeInput: String = "",
    val errorMessage: String? = null
)