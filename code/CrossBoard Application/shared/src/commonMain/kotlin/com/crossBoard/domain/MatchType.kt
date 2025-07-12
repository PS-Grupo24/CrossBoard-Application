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
    companion object {
        /**
         * List of the currently implemented match types.
         * Inserting a new match type before making sure all the steps towards implementing
         * were completed may result in runtime errors.
         */
        val availableTypes = setOf(TicTacToe, Reversi)
    }
}

/**
 * Function to convert a String to a MatchType.
 * @return MatchType the MatchType corresponding to the String.
 */
fun String.toMatchType(): MatchType =
    MatchType.valueOf(this)