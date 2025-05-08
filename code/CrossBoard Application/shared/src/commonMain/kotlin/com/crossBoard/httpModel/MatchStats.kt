package com.crossBoard.httpModel

import kotlinx.serialization.Serializable

@Serializable
data class MatchStatsOutput(
    val matchType: String,
    val numberOfGames: Int,
    val numberOfWins: Int,
    val numberOfDraws: Int,
    val numberOfLosses: Int,
    val averageWinningRate: Double
)