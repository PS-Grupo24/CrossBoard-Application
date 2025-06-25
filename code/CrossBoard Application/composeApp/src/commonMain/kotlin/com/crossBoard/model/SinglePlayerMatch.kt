package com.crossBoard.model

import com.crossBoard.domain.board.Board
import com.crossBoard.domain.board.BoardDraw
import com.crossBoard.domain.board.BoardRun
import com.crossBoard.domain.board.BoardWin
import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.move.Move
import com.crossBoard.domain.Player
import com.crossBoard.domain.board.TicTacToeBoardRun
import com.crossBoard.domain.board.initialTicTacToePositions
import kotlin.random.Random

class SinglePlayerMatch(
    val id: Int,
    val board: Board,
    val state: MatchState,
    val matchType: MatchType,
    val version: Int
) {
    companion object {
        fun startGame(matchType: MatchType): SinglePlayerMatch {
            return when (matchType) {
                MatchType.TicTacToe -> {
                    val p1 = Player.random()
                    val board = TicTacToeBoardRun(
                        initialTicTacToePositions(),
                        emptyList(),
                        Player.random(),
                        p1,
                        p1.other(),
                    )
                    SinglePlayerMatch(
                        Random.nextInt(),
                        board,
                        MatchState.RUNNING,
                        matchType,
                        1
                    )
                }
            }
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