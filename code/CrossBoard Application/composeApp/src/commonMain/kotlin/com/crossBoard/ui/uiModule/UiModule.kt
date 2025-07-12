package com.crossBoard.ui.uiModule

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.board.Board
import com.crossBoard.domain.move.Move

/**
 * A contract for a self-contained UI module responsible for rendering a specific type of game
 * and translating user input into game-specific moves.
 *
 * This interface uses a generic type `M` which must be a subtype of [Move], allowing each
 * implementation to work with its specific move type (e.g., `TicTacToeMove`, `ReversiMove`).
 *
 * @param M The specific [Move] subclass that this UI module will create and handle.
 */
 interface UiModule<B: Board, M: Move> {
    /**
     * The unique [MatchType] that this module is responsible for.
     * This property acts as a key for looking up and selecting the correct UI module
     * for a given game.
     */
    val matchType: MatchType
    /**
     * A [Composable] function that renders the visual representation of a game board.
     *
     * This function is the core of the UI module. It is responsible for taking a generic [B] state
     * and displaying it appropriately for its specific game type. Crucially, it must also handle
     * user interactions (like clicks) and construct the correct, game-specific [M] object.
     *
     * @param board The current state of the game board to be displayed. The implementation
     *              will need to cast this to its specific board type (e.g., `TicTacToeBoard`).
     * @param myPlayerType The [Player] type (e.g., `Player.BLACK` or `Player.WHITE`) assigned to the
     *                     user who is currently viewing the board. This is used to determine
     *                     which symbols to draw and whose turn it is.
     * @param onMakeMove A high-level callback function that the view should invoke when the user
     *                   has performed an action that constitutes a valid move. The view is
     *                   responsible for constructing the specific move object `M` and passing
     *                   it to this callback.
     * @param enabled A flag indicating whether the board should accept user input. If `false`,
     *                the view should be non-interactive (e.g., no click handling).
     * @param modifier The [Modifier] to be applied to the root container of the board view,
     *                 allowing for external customization of layout, size, etc.
     */
    @Composable
    fun BoardView(
        board: B,
        myPlayerType: Player,
        onMakeMove: (move: M) -> Unit,
        enabled: Boolean = true,
        modifier: Modifier = Modifier
    )

    fun generateRandomMachineMove(board: B, machinePlayerType: Player): M
}