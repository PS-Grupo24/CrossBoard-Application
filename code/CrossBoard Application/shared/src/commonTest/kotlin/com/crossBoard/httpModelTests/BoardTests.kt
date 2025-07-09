package com.crossBoard.httpModelTests

import com.crossBoard.domain.Column
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.Row
import com.crossBoard.domain.Square
import com.crossBoard.domain.board.*
import com.crossBoard.domain.move.ReversiMove
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.domain.position.ReversiPosition
import com.crossBoard.domain.position.TicPosition
import com.crossBoard.httpModel.toBoard
import com.crossBoard.httpModel.toBoardOutput

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BoardOutputTest {

    private val p1 = Player.BLACK
    private val p2 = Player.WHITE

    private val ticType = MatchType.TicTacToe.toString()
    private val revType = MatchType.Reversi.toString()
    @Test
    fun `TicTacToe RUNNING toBoard and back to BoardOutput`() {
        val pos = listOf(
            TicPosition(Player.BLACK, Square(Row(0, 3), Column('a'))),
            TicPosition(Player.WHITE, Square(Row(1, 3), Column('b')))
        )
        val mov = listOf(
            TicTacToeMove(Player.BLACK, Square(Row(0, 3), Column('a'))),
            TicTacToeMove(Player.WHITE, Square(Row(1, 3), Column('b')))
        )

        val board = TicTacToeBoardRun(pos, mov, p1, p1, p2)
        val output = board.toBoardOutput(null, MatchType.TicTacToe)

        val restored = output.toBoard(ticType, "RUNNING")

        assertTrue(restored is TicTacToeBoardRun)
        assertEquals(board.positions, restored.positions)
        assertEquals(board.moves, restored.moves)
        assertEquals(board.turn, restored.turn)
    }

    @Test
    fun `TicTacToe WIN toBoard and back to BoardOutput`() {
        val pos = listOf(
            TicPosition(p1, Square(Row(0, 3), Column('a')))
        )
        val mov = listOf(
            TicTacToeMove(p1, Square(Row(0, 3), Column('a')))
        )

        val board = TicTacToeBoardWin(p1, pos, mov, p2, p1, p2)
        val output = board.toBoardOutput(board.winner, MatchType.TicTacToe)
        val restored = output.toBoard(ticType,  "WIN")

        assertTrue(restored is TicTacToeBoardWin)
        assertEquals(p1, restored.winner)
    }

    @Test
    fun `TicTacToe DRAW toBoard and back`() {
        val pos = emptyList<TicPosition>()
        val mov = emptyList<TicTacToeMove>()

        val board = TicTacToeBoardDraw(pos, mov, p1, p1, p2)
        val output = board.toBoardOutput(null, MatchType.TicTacToe)
        val restored = output.toBoard(ticType, "DRAW")

        assertTrue(restored is TicTacToeBoardDraw)
    }

    @Test
    fun `Reversi RUNNING toBoard and back to BoardOutput`() {
        val pos = listOf(
            ReversiPosition(p1, Square(Row(3, 8), Column('d'))),
            ReversiPosition(p2, Square(Row(4, 8), Column('e')))
        )
        val mov = listOf(
            ReversiMove(p1, Square(Row(2, 8), Column('d')))
        )

        val board = ReversiBoardRun(pos, mov, p2, p1, p2)
        val output = board.toBoardOutput(null, MatchType.Reversi)
        val restored = output.toBoard(revType, "RUNNING")

        assertTrue(restored is ReversiBoardRun)
        assertEquals(board.positions, restored.positions)
        assertEquals(board.moves, restored.moves)
        assertEquals(board.turn, restored.turn)
    }

    @Test
    fun `Reversi WIN toBoard and back`() {
        val pos = listOf(
            ReversiPosition(p2, Square(Row(7, 8), Column('h')))
        )
        val mov = listOf(
            ReversiMove(p2, Square(Row(7, 8), Column('h')))
        )

        val board = ReversiBoardWin(p2, pos, mov, p1, p1, p2)
        val output = board.toBoardOutput(p2, MatchType.Reversi)
        val restored = output.toBoard(revType, "WIN")

        assertTrue(restored is ReversiBoardWin)
        assertEquals(p2, restored.winner)
    }

    @Test
    fun `Reversi DRAW toBoard and back`() {
        val board = ReversiBoardDraw(
            positions = emptyList(),
            moves = emptyList(),
            turn = p2,
            player1 = p1,
            player2 = p2
        )
        val output = board.toBoardOutput(null, MatchType.Reversi)
        val restored = output.toBoard(revType, "DRAW")

        assertTrue(restored is ReversiBoardDraw)
    }
}
