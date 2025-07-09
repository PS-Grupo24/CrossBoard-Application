package com.crossBoard.domainTests.moveTests

import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.board.ReversiBoard
import com.crossBoard.domain.board.TicTacToeBoard
import com.crossBoard.domain.move.ReversiMove
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.domain.move.moveToString
import com.crossBoard.domain.move.toMove
import com.crossBoard.domain.toSquare
import com.crossBoard.httpModel.ReversiMoveOutput
import com.crossBoard.httpModel.TicTacToeMoveOutput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MoveTests {

    @Test
    fun `moveToString produces expected output`() {
        val ticMove = TicTacToeMove(
            player = Player.WHITE,
            square = "1a".toSquare(TicTacToeBoard.Companion.BOARD_DIM)
        )
        val revMove = ReversiMove(
            player = Player.BLACK,
            square = "8h".toSquare(ReversiBoard.Companion.BOARD_DIM)
        )

        assertEquals("WHITE,1a", moveToString(ticMove, MatchType.TicTacToe))
        assertEquals("BLACK,8h", moveToString(revMove, MatchType.Reversi))
    }

    @Test
    fun `String toMove parses TicTacToe correctly`() {
        val input = "BLACK,2b"
        val move = input.toMove(MatchType.TicTacToe)
        assertTrue(move is TicTacToeMove)
        assertEquals(Player.BLACK, move.player)
        assertEquals("2b", move.square.toString())
    }

    @Test
    fun `String toMove parses Reversi correctly`() {
        val input = "WHITE,5c"
        val move = input.toMove(MatchType.Reversi)
        assertTrue(move is ReversiMove)
        assertEquals(Player.WHITE, move.player)
        assertEquals("5c", move.square.toString())
    }

    @Test
    fun `MoveOutput toMove converts TicTacToeMoveOutput correctly`() {
        val output = TicTacToeMoveOutput(player = "BLACK", square = "3a")
        val move = output.toMove(MatchType.TicTacToe)
        assertTrue(move is TicTacToeMove)
        assertEquals(Player.BLACK, move.player)
        assertEquals("3a", move.square.toString())
    }

    @Test
    fun `MoveOutput toMove converts ReversiMoveOutput correctly`() {
        val output = ReversiMoveOutput(player = "WHITE", square = "4d")
        val move = output.toMove(MatchType.Reversi)
        assertTrue(move is ReversiMove)
        assertEquals(Player.WHITE, move.player)
        assertEquals("4d", move.square.toString())
    }
}