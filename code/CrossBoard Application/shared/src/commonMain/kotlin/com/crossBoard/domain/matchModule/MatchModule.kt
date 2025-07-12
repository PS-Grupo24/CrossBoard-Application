package com.crossBoard.domain.matchModule

import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.Square
import com.crossBoard.domain.board.Board
import com.crossBoard.domain.move.Move
import com.crossBoard.domain.position.Position
import com.crossBoard.httpModel.moveInput.MoveInput
import com.crossBoard.httpModel.moveOutput.MoveOutput


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
 * @param B  Board type for the game (must implement `Board`)
 * @param M  Move type for the game (must implement `Move`)
 * @param P  Position type for the game (must implement `Position`)
 * @param MI Move input data structure (usually received from clients)
 * @param MO Move output data structure (usually returned to clients)
 */
interface MatchModule<
        B : Board,
        M : Move,
        P : Position,
        MI : MoveInput,
        MO : MoveOutput
        >
{
    /**
     * The match type this module supports (e.g., TicTacToe, Reversi).
     */
    val matchType: MatchType

    /**
     * Converts a move input (e.g., from a client) into a move object.
     *
     * @param input The move input.
     * @return The corresponding move.
     */
    fun moveInputToMove(input: MI): M


    /**
     * Converts a move object to its corresponding output format.
     *
     * @param move The move to convert.
     * @return The move output (usually sent to clients).
     */
    fun moveToMoveOutput(move: M): MO

    /**
     * Converts a move output back to a move object.
     *
     * @param move The move output.
     * @return The internal move representation.
     */
    fun moveOutputToMove(move: MO): M

    /**
     * Provides the initial board state for a new match.
     *
     * @return The initialized board.
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

    /**
     * Constructs a move input from the given player and board coordinates.
     * @param move The move [M] to convert into [MI]
     * @return The [Move] input object.
     */
    fun moveToMoveInput(move: M): MI

    /**
     * Converts a board output (typically from storage or network) into an actual board object.
     *
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

