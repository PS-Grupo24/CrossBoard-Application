package com.crossBoard.model

import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.move.Move
import com.crossBoard.domain.Player
import com.crossBoard.domain.board.*
import com.crossBoard.domain.matchModule.ModuleProvider
import kotlin.random.Random

/**
 * Class ´SinglePlayerMatch´ responsible for the Singleplayer Match logic.
 * @property id The id of the match.
 * @property board The board of the match.
 * @property state The state of the match.
 * @property matchType The type of the match.
 * @property version The version of the match, used to track changes.
 */
class SinglePlayerMatch(
    val id: Int,
    val board: Board,
    val state: MatchState,
    val matchType: MatchType,
    val version: Int
) {
    companion object {

        /**
         * Starts a new game with the given match type.
         * @param matchType The type of the match to start.
         * @return A new instance of SinglePlayerMatch for the new singleplayerMatch.
         */
        fun startGame(matchType: MatchType): SinglePlayerMatch {
            val module = ModuleProvider.getModule(matchType)
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

    /**
     * Function ´makeMove´ responsible for making a move in the match.
     * @param move The move to be made.
     * @return A new instance of SinglePlayerMatch with the move made.
     */
    fun makeMove(move: Move): SinglePlayerMatch {
        val newBoard = board.play(move)
        return SinglePlayerMatch(
            id,
            newBoard,
            getMatchStateFromBoard(newBoard),
            matchType,
            version + 1
        )
    }

    /**
     * Function ´forfeit´ responsible for forfeiting the match.
     * @param player The player who is forfeiting the match.
     * @return A new instance of SinglePlayerMatch with the match forfeited.
     */
    fun forfeit(player: Player): SinglePlayerMatch {
        val newBoard = board.forfeit(player)
        return SinglePlayerMatch(
            id,
            newBoard,
            MatchState.WIN,
            matchType,
            version + 1
        )
    }

    /**
     * Function ´equals´ responsible for checking if two SinglePlayerMatch instances are equal.
     * @param other The other instance to compare with.
     * @return True if the instances are equal, false otherwise.
     */
    override fun equals(other: Any?) = other is SinglePlayerMatch && id == other.id && other.version == version

    /**
     * Function ´hashCode´ responsible for generating a hash code for the SinglePlayerMatch instance.
     * @return The hash code of the SinglePlayerMatch instance.
     */
    override fun hashCode(): Int = id.hashCode() + version.hashCode()
}

/**
 * Function ´getMatchStateFromBoard´ responsible for getting the match state from the board.
 * @param board The board of the match.
 * @return The match state of the match based on the board.
 * @throws IllegalArgumentException if the board is not a valid type.
 */
private fun getMatchStateFromBoard(board: Board): MatchState = when(board){
        is BoardRun -> MatchState.RUNNING
        is BoardWin -> MatchState.WIN
        is BoardDraw -> MatchState.DRAW
        else -> throw IllegalArgumentException("Invalid MatchState: $board")
}