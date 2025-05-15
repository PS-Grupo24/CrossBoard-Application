package com.crossBoard.domain

/**
 * Constants representing the different states of a match.
 * @param WAITING_MATCH the match is waiting for a player.
 * @param RUNNING_MATCH the match is running.
 * @param DRAW_MATCH the match is a draw.
 * @param WIN_MATCH the match is won.
 */
const val RUNNING_MATCH = "RUNNING"
const val DRAW_MATCH = "DRAW"
const val WIN_MATCH = "WIN"
const val WAITING_MATCH = "WAITING"

/**
 * Enum class representing the different states of a match.
 * @property value the string representation of the match state.
 */
enum class MatchState(val value: String) {
    WAITING(WAITING_MATCH),
    RUNNING(RUNNING_MATCH),
    DRAW(DRAW_MATCH),
    WIN(WIN_MATCH);

    /**
     * Override toString method to return the string representation of the match state.
     * @return the string representation of the match state.
     */
    override fun toString(): String = value
}

/**
 * Extension function to convert a string to a MatchState.
 * @return the MatchState corresponding to the string.
 */
fun String.toMatchState(): MatchState = when(this) {
        WAITING_MATCH -> MatchState.WAITING
        RUNNING_MATCH -> MatchState.RUNNING
        DRAW_MATCH -> MatchState.DRAW
        WIN_MATCH -> MatchState.WIN
        else -> throw IllegalArgumentException("Invalid MatchState: $this")
    }

/**
 * Function getMatchStateFromBoard to get the match state of the match.
 * @param player2 the second player.
 * @param board the board of the match.
 * @return MatchState the match state of the match.
 */
fun getMatchStateFromBoard(player2: Int?, board: Board): MatchState = when(board) {
        is BoardRun -> {
            if (player2 == null) MatchState.WAITING
            else MatchState.RUNNING
        }
        is BoardDraw -> MatchState.DRAW
        is BoardWin -> MatchState.WIN
        else -> throw IllegalArgumentException("Invalid MatchState: $board")
    }