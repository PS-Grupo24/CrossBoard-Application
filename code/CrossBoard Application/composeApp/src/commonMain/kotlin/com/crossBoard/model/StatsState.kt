package com.crossBoard.model

import com.crossBoard.httpModel.MatchStatsOutput

data class StatsState(
    val stats: List<MatchStatsOutput> = emptyList(),
    val errorMessage: String? = null
)