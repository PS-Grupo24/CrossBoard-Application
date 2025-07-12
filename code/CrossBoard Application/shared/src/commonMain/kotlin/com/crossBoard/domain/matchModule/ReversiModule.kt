package com.crossBoard.domain.matchModule

import com.crossBoard.domain.Column
import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.Row
import com.crossBoard.domain.Square
import com.crossBoard.domain.board.ReversiBoard
import com.crossBoard.domain.board.ReversiBoardDraw
import com.crossBoard.domain.board.ReversiBoardRun
import com.crossBoard.domain.board.ReversiBoardWin
import com.crossBoard.domain.board.initialReversiPositions
import com.crossBoard.domain.move.ReversiMove
import com.crossBoard.domain.position.ReversiPosition
import com.crossBoard.domain.toPlayer
import com.crossBoard.domain.toSquare
import com.crossBoard.httpModel.BoardOutput
import com.crossBoard.httpModel.moveInput.ReversiMoveInput
import com.crossBoard.httpModel.moveOutput.ReversiMoveOutput

/**
 * Implementation of the [MatchModule] interface for the Reversi game.
 *
 * This module encapsulates all Reversi-specific logic required to support match operations,
 * including conversion between domain types ([ReversiMove], [ReversiPosition], [ReversiBoard])
 * and HTTP layer representations ([ReversiMoveInput], [ReversiMoveOutput]),
 * along with game state restoration and initial board setup.
 *
 * Responsibilities:
 * - Construct the initial Reversi board with randomized players.
 * - Convert between different move representations (input/output/string).
 * - Map incoming data from clients (via [BoardOutput]) back into domain board states.
 * - Support serialization/deserialization of positions and moves for game state tracking.
 *
 * This module is registered under the [MatchType.Reversi] identifier.
 */
class ReversiModule : MatchModule<
        ReversiBoard,
        ReversiMove,
        ReversiPosition,
        ReversiMoveInput,
        ReversiMoveOutput
        >
{
    override val matchType: MatchType
        get() = MatchType.Reversi

    override fun getInitialBoard(): ReversiBoard {
        val p1 = Player.random()
        return ReversiBoardRun(
            initialReversiPositions(),
            emptyList(),
            Player.random(),
            p1,
            p1.other(),
        )
    }

    override fun moveInputToMove(input: ReversiMoveInput): ReversiMove {
        return ReversiMove(input.player.toPlayer(), input.square.toSquare(ReversiBoard.BOARD_DIM))
    }

    override fun moveToMoveOutput(move: ReversiMove): ReversiMoveOutput {
        return ReversiMoveOutput(move.player.toString(), move.square.toString())
    }

    override fun moveOutputToMove(move: ReversiMoveOutput): ReversiMove {
        return ReversiMove(move.player.toPlayer(), move.square.toSquare(ReversiBoard.BOARD_DIM))
    }
    override fun getSquare(rowIndex: Int, columnIndex: Int): Square {
        return Square(Row(rowIndex, ReversiBoard.BOARD_DIM), Column('a' + columnIndex))
    }

    override fun stringToPosition(input: String): ReversiPosition {
        val values = input.split(",")
        return ReversiPosition(values[0].toPlayer(), values[1].toSquare(ReversiBoard.BOARD_DIM))
    }

    override fun positionToString(position: ReversiPosition): String {
        return position.toString()
    }

    override fun moveToMoveInput(move: ReversiMove): ReversiMoveInput {
        return ReversiMoveInput(
            player = move.player.toString(),
            "${move.square.row.number}${move.square.column.symbol}",
        )
    }

    override fun getBoard(
        positions: List<ReversiPosition>,
        moves: List<ReversiMove>,
        player1: Player,
        player2: Player,
        turn: Player,
        winner: Player?,
        state: MatchState
    ): ReversiBoard {
        return when(state) {
            MatchState.RUNNING -> {
                ReversiBoardRun(
                    positions,
                    moves,
                    turn,
                    player1,
                    player2
                )
            }
            MatchState.WAITING -> {
                ReversiBoardRun(
                    positions,
                    moves,
                    turn,
                    player1,
                    player2
                )
            }
            MatchState.WIN -> ReversiBoardWin(
                winner ?: throw IllegalArgumentException("Winner must not be null"),
                positions,
                moves,
                turn,
                player1,
                player2,
            )
            MatchState.DRAW -> ReversiBoardDraw(
                positions,
                moves,
                turn,
                player1,
                player2,
            )
        }
    }
}