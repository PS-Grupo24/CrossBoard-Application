package com.crossBoard.domain

/**
 * Enum class "MatchType" represents the type of the game.
 * @property TicTacToe represents the Tic Tac Toe game.
 * @property Reversi represents the Reversi game.
 */
enum class MatchType() {
    TicTacToe,
    Reversi;

    override fun toString(): String = name
}

/**
 * Function to convert a String to a MatchType.
 * @return MatchType the MatchType corresponding to the String.
 */
fun String.toMatchType(): MatchType =
    MatchType.valueOf(this)