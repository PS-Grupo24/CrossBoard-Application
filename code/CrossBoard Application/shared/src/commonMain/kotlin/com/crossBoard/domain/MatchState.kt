package com.crossBoard.domain

const val RUNNING_MATCH = "RUNNING"
const val DRAW_MATCH = "DRAW"
const val WIN_MATCH = "WIN"
const val WAITING_MATCH = "WAITING"

enum class MatchState(val value: String) {
    WAITING(WAITING_MATCH),
    RUNNING(RUNNING_MATCH),
    DRAW(DRAW_MATCH),
    WIN(WIN_MATCH);

    override fun toString(): String = value
}

fun String.toMatchState(): MatchState {
    return when(this){
        WAITING_MATCH -> MatchState.WAITING
        RUNNING_MATCH -> MatchState.RUNNING
        DRAW_MATCH -> MatchState.DRAW
        WIN_MATCH -> MatchState.WIN
        else -> throw IllegalArgumentException("Invalid MatchState: $this")
    }
}

fun getMatchStateFromBoard(board: Board): MatchState {
    return when(board){
        is BoardRun -> MatchState.RUNNING
        is BoardDraw -> MatchState.DRAW
        is BoardWin -> MatchState.WIN
        else -> throw IllegalArgumentException("Invalid MatchState: $board")
    }
}