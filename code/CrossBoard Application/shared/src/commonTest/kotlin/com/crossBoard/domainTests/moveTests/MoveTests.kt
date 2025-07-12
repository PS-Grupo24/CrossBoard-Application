package com.crossBoard.domainTests.moveTests

import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.move.ReversiMove
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.domain.move.toMove
import com.crossBoard.httpModel.moveOutput.ReversiMoveOutput
import com.crossBoard.httpModel.moveOutput.TicTacToeMoveOutput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MoveTests {

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