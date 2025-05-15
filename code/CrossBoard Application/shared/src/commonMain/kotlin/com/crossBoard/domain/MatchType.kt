package com.crossBoard.domain

//Constant representing the type of the match.
const val TIC_VALUE = "tic"

/**
 * Enum class "MatchType" represents the type of the game.
 * @param value the String value of each MatchType.
 * @property TicTacToe represents the Tic Tac Toe game.
 */
enum class MatchType(val value: String) {
    TicTacToe(TIC_VALUE);

    override fun toString(): String = value
}

/**
 * Function to convert a String to a MatchType.
 * @return MatchType the MatchType corresponding to the String.
 */
fun String.toMatchType(): MatchType =
    when(this) {
        TIC_VALUE -> MatchType.TicTacToe
        else -> throw IllegalArgumentException("Wrong MatchType $this")
    }