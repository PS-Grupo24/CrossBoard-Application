package com.crossBoard.ui.uiModule

import com.crossBoard.domain.MatchType
import com.crossBoard.domain.board.Board
import com.crossBoard.domain.move.Move

/**
 * A centralized registry that holds all available UIModule implementations.
 */
object UiModuleProvider {
    private val uiModules = listOf(
        TicUiModule(),
        RevUiModule()
    )

    private val uiModuleMap: Map<MatchType, UiModule<*, *>> by lazy {
        uiModules.associateBy { it.matchType }
    }

    /**
     * Retrieves the UIModule for a given MatchType.
     *
     * @param matchType The type of game.
     * @return The corresponding UIModule.
     * @throws IllegalArgumentException if no module is found for the given type.
     */
    @Suppress("UNCHECKED_CAST") // This is now safe and necessary
    fun <B: Board, M : Move> getModule(matchType: MatchType): UiModule<B, M> {
        val module = uiModuleMap[matchType]
            ?: throw IllegalArgumentException("No UIModule found for MatchType: $matchType")

        // The unchecked cast warning is acceptable here because our internal map
        // logic guarantees the type correlation.
        return module as UiModule<B, M>
    }
}