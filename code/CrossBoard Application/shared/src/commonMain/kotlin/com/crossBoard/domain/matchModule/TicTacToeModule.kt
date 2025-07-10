package com.crossBoard.domain.matchModule

import com.crossBoard.domain.Column
import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.Row
import com.crossBoard.domain.Square
import com.crossBoard.domain.board.TicTacToeBoard
import com.crossBoard.domain.board.TicTacToeBoardDraw
import com.crossBoard.domain.board.TicTacToeBoardRun
import com.crossBoard.domain.board.TicTacToeBoardWin
import com.crossBoard.domain.board.initialTicTacToePositions
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.domain.move.toMove
import com.crossBoard.domain.position.TicPosition
import com.crossBoard.domain.position.toPosition
import com.crossBoard.domain.toMatchState
import com.crossBoard.domain.toPlayer
import com.crossBoard.domain.toSquare
import com.crossBoard.httpModel.BoardOutput
import com.crossBoard.httpModel.TicTacToeMoveInput
import com.crossBoard.httpModel.TicTacToeMoveOutput

/**
 * Implementation of [MatchModule] for the Tic-Tac-Toe game.
 *
 * This module defines the Tic-Tac-Toe-specific logic required for managing game state,
 * parsing and serializing moves, and transforming input/output between HTTP models and
 * domain-level representations.
 *
 * Key Responsibilities:
 * - Convert between domain types such as [TicTacToeMove], [TicPosition], and [TicTacToeBoard].
 * - Handle serialization/deserialization of moves and positions from strings or HTTP representations.
 * - Provide the initial board configuration with randomized players.
 * - Restore game state from a [BoardOutput] and match state string.
 * - Identify itself via [matchType] as [MatchType.TicTacToe], so it can be dynamically discovered and used.
 *
 * This class ensures that the core game logic is cleanly separated and reusable,
 * enabling polymorphic handling of different game types within the application.
 */
class TicTacToeModule : MatchModule<
        TicTacToeBoard,
        TicTacToeMove,
        TicPosition,
        TicTacToeMoveInput,
        TicTacToeMoveOutput
        >
{
    override val matchType: MatchType = MatchType.TicTacToe

    override fun moveInputToMove(input: TicTacToeMoveInput): TicTacToeMove {
        return TicTacToeMove(input.player.toPlayer(), input.square.toSquare(TicTacToeBoard.BOARD_DIM))
    }

    override fun moveToMoveOutput(move: TicTacToeMove): TicTacToeMoveOutput {
        return TicTacToeMoveOutput(move.player.toString(), move.square.toString())
    }

    override fun getInitialBoard(): TicTacToeBoard {
        val p1 = Player.random()
        return TicTacToeBoardRun(
            initialTicTacToePositions(),
            emptyList(),
            Player.random(),
            p1,
            p1.other(),
        )
    }

    override fun moveOutputToMove(move: TicTacToeMoveOutput): TicTacToeMove {
        return TicTacToeMove(move.player.toPlayer(), move.square.toSquare(TicTacToeBoard.BOARD_DIM))
    }

    override fun moveToString(move: TicTacToeMove): String {
        return "${move.player},${move.square}"
    }

    override fun stringToMove(string: String): TicTacToeMove {
        val values = string.split(",")
        return TicTacToeMove(values[0].toPlayer(), values[1].toSquare(TicTacToeBoard.BOARD_DIM))
    }

    override fun getSquare(rowIndex: Int, columnIndex: Int): Square {
        return Square(Row(rowIndex, TicTacToeBoard.BOARD_DIM), Column('a' + columnIndex))
    }

    override fun stringToPosition(input: String): TicPosition {
        val values = input.split(",")
        return TicPosition(values[0].toPlayer(), values[1].toSquare(TicTacToeBoard.BOARD_DIM))
    }

    override fun getMoveInput(playerType: Player, rowNumber: Int, columnChar: Char): TicTacToeMoveInput {
        return TicTacToeMoveInput(
            playerType.toString(),
            "$rowNumber$columnChar",
        )
    }

    override fun boardOutputToBoard(board: BoardOutput, state: String): TicTacToeBoard {
        val tur = board.turn.toPlayer()
        val player1 = board.player1.toPlayer()
        val player2 = player1.other()

        val pos = board.positions.map {
            it.toPosition(matchType) as TicPosition
        }
        val mov = board.moves.map { it.toMove(matchType) as TicTacToeMove }

        return when(state.toMatchState()){
            MatchState.RUNNING -> {
                TicTacToeBoardRun(
                    pos,
                    mov,
                    tur,
                    player1,
                    player2
                )
            }
            MatchState.WAITING -> {
                TicTacToeBoardRun(
                    pos,
                    mov,
                    tur,
                    player1,
                    player2
                )
            }
            MatchState.WIN -> TicTacToeBoardWin(
                board.winner?.toPlayer() ?: throw IllegalArgumentException("Winner must not be null"),
                pos,
                mov,
                tur,
                player1,
                player2,
            )
            MatchState.DRAW-> TicTacToeBoardDraw(
                pos,
                mov,
                tur,
                player1,
                player2,
            )
        }
    }
}