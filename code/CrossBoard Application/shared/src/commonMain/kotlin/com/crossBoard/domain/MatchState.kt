package com.crossBoard.domain

import com.crossBoard.domain.board.Board
import com.crossBoard.domain.board.BoardDraw
import com.crossBoard.domain.board.BoardRun
import com.crossBoard.domain.board.BoardWin

/**
 * Constants representing the different states of a match.
 */

/**
 * Enum class representing the different states of a match.
 */
enum class MatchState() {
    WAITING,
    RUNNING,
    DRAW,
    WIN;

    /**
     * Override toString method to return the string representation of the match state.
     * @return the string representation of the match state.
     */
    override fun toString(): String = this.name
}

/**
 * Extension function to convert a string to a MatchState.
 * @return the MatchState corresponding to the string.
 */
fun String.toMatchState(): MatchState =
        MatchState.valueOf(this)


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