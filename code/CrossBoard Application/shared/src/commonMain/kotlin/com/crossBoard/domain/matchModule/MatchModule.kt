package com.crossBoard.domain.matchModule

import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.Square
import com.crossBoard.domain.board.Board
import com.crossBoard.domain.board.BoardRun
import com.crossBoard.domain.board.BoardWin
import com.crossBoard.domain.board.BoardDraw
import com.crossBoard.domain.move.Move
import com.crossBoard.domain.position.Position
import com.crossBoard.httpModel.moveHttp.MoveHttp


/**
 * The `MatchModule` interface defines the core game-specific logic required to support a new match type
 * (e.g., TicTacToe, Reversi, etc.). Implementing this interface allows your match type to integrate
 * with the multiplayer match engine by providing all necessary transformations and behaviors.
 *
 * Each new match type must implement this interface to handle its own:
 * - Board representation
 * - Move parsing and transformation
 * - Positioning logic
 * - Input/output translation for moves
 *
 * Responsibilities of a `MatchModule` include:
 * - Converting between serialized forms and typed objects (`Move`, `Board`, etc.)
 * - Providing the initial board state
 * - Mapping between UI/user inputs and internal models
 * - Converting between API-level input/output and game logic
 *
 * Type Parameters:
 * @param B  [Board] type for the game.
 * @param M  [Move] type for the move.
 * @param P  [Position] type for the position.
 * @param MH [MoveHttp] type move format to be sent in http messages.
 */
interface MatchModule<
        B : Board,
        M : Move,
        P : Position,
        MH: MoveHttp
        >
{
    /**
     * The match type this module supports (e.g., TicTacToe, Reversi).
     */
    val matchType: MatchType
    /**
     * Provides the initial board state for a new match.
     *
     * It is required to return the [BoardRun] for this [MatchType] with
     * the initial positions, moves, player types for player1, player2 and turn.
     *
     * @return The [BoardRun] for this new [MatchType].
     */
    fun getInitialBoard(): B

    /**
     * Returns the `Square` at a specific board position.
     *
     * @param rowIndex The row index.
     * @param columnIndex The column index.
     * @return The [Square] at the given coordinates.
     */
    fun getSquare(rowIndex: Int, columnIndex: Int): Square


    /**
     * Deserializes a string into a [Position] object.
     *
     * @param input The string representation of the position.
     * @return The [Position] object.
     */
    fun stringToPosition(input: String): P

    /**
     * Serializes a [Position] into [String]
     *
     * @param position The position [P] to serialize.
     * @return [String] The serialized position.
     */
    fun positionToString(position: P): String

    fun moveToMoveHttp(move: M): MH

    fun moveHttpToMove(move: MH): M

    /**
     * Converts a board output (typically from storage or network) into an actual board object.
     *
     * When the state is [MatchState.RUNNING] or [MatchState.WAITING] should return the [BoardRun] of this [MatchType].
     * When the state is [MatchState.WIN] should return the [BoardWin] of this [MatchType].
     * When the state is [MatchState.DRAW] should return the [BoardDraw] of this [MatchType].
     * @param state The current match state (e.g., RUNNING, DRAW).
     * @return The reconstructed board.
     */
    fun getBoard(
        positions: List<P>,
        moves: List<M>,
        player1: Player,
        player2: Player,
        turn: Player,
        winner: Player?,
        state: MatchState,
    ): B
}

