package com.crossBoard.model

import com.crossBoard.httpModel.MatchStats

data class StatsState(
    val stats: List<MatchStats> = emptyList(),
    val errorMessage: String? = null
)