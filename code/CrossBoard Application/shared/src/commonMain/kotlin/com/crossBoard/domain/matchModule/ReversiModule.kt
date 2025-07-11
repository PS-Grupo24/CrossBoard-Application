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
import com.crossBoard.domain.move.toMove
import com.crossBoard.domain.position.ReversiPosition
import com.crossBoard.domain.position.toPosition
import com.crossBoard.domain.toMatchState
import com.crossBoard.domain.toPlayer
import com.crossBoard.domain.toSquare
import com.crossBoard.httpModel.BoardOutput
import com.crossBoard.httpModel.ReversiMoveInput
import com.crossBoard.httpModel.ReversiMoveOutput

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

    override fun moveToString(move: ReversiMove): String {
        return "${move.player},${move.square}"
    }

    override fun stringToMove(string: String): ReversiMove {
        val values = string.split(",")
        return ReversiMove(values[0].toPlayer(), values[1].toSquare(ReversiBoard.BOARD_DIM))
    }

    override fun getSquare(rowIndex: Int, columnIndex: Int): Square {
        return Square(Row(rowIndex, ReversiBoard.BOARD_DIM), Column('a' + columnIndex))
    }

    override fun stringToPosition(input: String): ReversiPosition {
        val values = input.split(",")
        return ReversiPosition(values[0].toPlayer(), values[1].toSquare(ReversiBoard.BOARD_DIM))
    }

    override fun moveToMoveInput(move: ReversiMove): ReversiMoveInput {
        return ReversiMoveInput(
            player = move.player.toString(),
            "${move.square.row.number}${move.square.column.symbol}",
        )
    }

    override fun boardOutputToBoard(board: BoardOutput, state: String): ReversiBoard {
        val tur = board.turn.toPlayer()
        val player1 = board.player1.toPlayer()
        val player2 = player1.other()
        val pos = board.positions.map{
            it.toPosition(matchType) as ReversiPosition
        }
        val mov = board.moves.map { it.toMove(matchType) as ReversiMove }

        return when(state.toMatchState()) {
            MatchState.RUNNING -> {
                ReversiBoardRun(
                    pos,
                    mov,
                    tur,
                    player1,
                    player2
                )
            }
            MatchState.WAITING -> {
                ReversiBoardRun(
                    pos,
                    mov,
                    tur,
                    player1,
                    player2
                )
            }
            MatchState.WIN -> ReversiBoardWin(
                board.winner?.toPlayer() ?: throw IllegalArgumentException("Winner must not be null"),
                pos,
                mov,
                tur,
                player1,
                player2,
            )
            MatchState.DRAW -> ReversiBoardDraw(
                pos,
                mov,
                tur,
                player1,
                player2,
            )
        }
    }
}