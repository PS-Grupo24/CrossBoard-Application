package com.crossBoard.domain.matchModule

import com.crossBoard.domain.MatchType
import com.crossBoard.domain.board.Board
import com.crossBoard.domain.move.Move
import com.crossBoard.domain.position.Position
import com.crossBoard.httpModel.MoveInput
import com.crossBoard.httpModel.MoveOutput

/**
 * The helper object that provides access to the modules and stores the available modules.
 */
object ModuleProvider {
    /**
     * List of the currently available Match modules.
     * It is required for any future modules of new match types to be inserted in this list
     * so the implemented functions can find their respective handler for the new match type.
     */
    private val modules: List<MatchModule<*, *, *, *, *>> = listOf(
        TicTacToeModule(),
        ReversiModule(),
    )

    private val uiModuleMap: Map<MatchType, MatchModule<*, *, *, *, *>> by lazy {
        modules.associateBy { it.matchType }
    }

    /**
     * Retrieves the UIModule for a given MatchType.
     *
     * @param matchType The type of game.
     * @return The corresponding UIModule.
     * @throws IllegalArgumentException if no module is found for the given type.
     */
    @Suppress("UNCHECKED_CAST")
    fun getModule(matchType: MatchType): MatchModule<Board, Move, Position, MoveInput, MoveOutput> {
        return (uiModuleMap[matchType]
            ?: throw IllegalArgumentException("No UIModule found for MatchType: $matchType")) as MatchModule<Board, Move, Position, MoveInput, MoveOutput>
    }
}