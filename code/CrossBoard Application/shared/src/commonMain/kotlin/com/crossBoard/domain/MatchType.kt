package com.crossBoard.domain


const val TIC_VALUE = "tic"

/**
 * Enum class "MatchType" represents the type of the game.
 * @param value the String value of each MatchType.
 * @property TicTacToe
 */
enum class MatchType(val value: String) {
    TicTacToe(TIC_VALUE);

    override fun toString(): String = value
}
fun String.toMatchType() : MatchType =
    when(this) {
        TIC_VALUE -> MatchType.TicTacToe
        else -> throw IllegalArgumentException("Wrong MatchType $this")
    }