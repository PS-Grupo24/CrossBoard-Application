package com.crossBoard.model

import com.crossBoard.httpModel.MatchStats

/**
 * StatsState responsible for tracking the resources used in the checking statistics functionality.
 * @param stats The stats for each type of match.
 * @param errorMessage The error message; `NULL` when no error found.
 */
data class StatsState(
    val stats: List<MatchStats> = emptyList(),
    val errorMessage: String? = null
)