package com.crossBoard.domainTests.moveTests

import com.crossBoard.domain.*
import com.crossBoard.domain.board.ReversiBoard
import com.crossBoard.domain.move.ReversiMove
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ReversiMoveTests {

    private val boardDim = ReversiBoard.BOARD_DIM

    @Test
    fun `ReversiMove equality and hashCode work for same values`() {
        val move1 = ReversiMove(Player.BLACK, "3c".toSquare(boardDim))
        val move2 = ReversiMove(Player.BLACK, "3c".toSquare(boardDim))

        assertEquals(move1, move2)
        assertEquals(move1.hashCode(), move2.hashCode())
    }

    @Test
    fun `ReversiMove inequality for different players`() {
        val move1 = ReversiMove(Player.BLACK, "3c".toSquare(boardDim))
        val move2 = ReversiMove(Player.WHITE, "3c".toSquare(boardDim))

        assertNotEquals(move1, move2)
    }

    @Test
    fun `ReversiMove inequality for different squares`() {
        val move1 = ReversiMove(Player.BLACK, "3c".toSquare(boardDim))
        val move2 = ReversiMove(Player.BLACK, "4c".toSquare(boardDim))

        assertNotEquals(move1, move2)
    }

    @Test
    fun `ReversiMove toString is meaningful`() {
        val move = ReversiMove(Player.WHITE, "5d".toSquare(boardDim))
        val expected = "ReversiMove(player=WHITE, square=5d)"

        assertEquals(expected, move.toString())
    }
}
