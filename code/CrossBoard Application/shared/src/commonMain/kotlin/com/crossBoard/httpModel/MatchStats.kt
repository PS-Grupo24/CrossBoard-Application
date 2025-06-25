package com.crossBoard.httpModel

import kotlinx.serialization.Serializable

/**
 * The format for the match statistics used in an HTTP message.
 * @param matchType The match type for the statistics.
 * @param numberOfMatches The total number of matches played for this match type.
 * @param numberOfWins The total number of matches won for this match type.
 * @param numberOfDraws The total number of draws for this match type.
 * @param numberOfLosses The total number of matches lost for this match type.
 * @param averageWinningRate The winning rate for this match type.
 */
@Serializable
data class MatchStats(
    val matchType: String,
    val numberOfMatches: Int,
    val numberOfWins: Int,
    val numberOfDraws: Int,
    val numberOfLosses: Int,
    val averageWinningRate: Double
)