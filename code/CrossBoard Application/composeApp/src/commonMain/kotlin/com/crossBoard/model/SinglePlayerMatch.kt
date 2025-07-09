package com.crossBoard.model

import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.move.Move
import com.crossBoard.domain.Player
import com.crossBoard.domain.board.*
import com.crossBoard.domain.matchModule.modules
import com.crossBoard.domain.move.ReversiMove
import kotlin.random.Random

/**
 * SinglePlayerMatch replicates a MultiplayerMatch.
 */
class SinglePlayerMatch(
    val id: Int,
    val board: Board,
    val state: MatchState,
    val matchType: MatchType,
    val version: Int
) {
    companion object {
        fun startGame(matchType: MatchType): SinglePlayerMatch {
            val module = modules.find { it.matchType == matchType } ?:
                throw IllegalArgumentException("No module found for match type : $matchType")
            val board = module.getInitialBoard()
            return SinglePlayerMatch(
                Random.nextInt(),
                board,
                MatchState.RUNNING,
                matchType,
                1
            )
        }
    }

    fun makeMove(move: Move): SinglePlayerMatch{
        val newBoard = board.play(move)
        return SinglePlayerMatch(
            id,
            newBoard,
            getMatchStateFromBoard(newBoard),
            matchType,
            version + 1
        )
    }

    fun forfeit(player: Player): SinglePlayerMatch{
        val newBoard = board.forfeit(player)
        return SinglePlayerMatch(
            id,
            newBoard,
            MatchState.WIN,
            matchType,
            version + 1
        )
    }

    override fun equals(other: Any?) = other is SinglePlayerMatch && id == other.id && other.version == version

    override fun hashCode(): Int = id.hashCode() + version.hashCode()
}

private fun getMatchStateFromBoard(board: Board): MatchState{
    return when(board){
        is BoardRun -> MatchState.RUNNING
        is BoardWin -> MatchState.WIN
        is BoardDraw -> MatchState.DRAW
        else -> throw IllegalArgumentException("Invalid MatchState: $board")
    }
}