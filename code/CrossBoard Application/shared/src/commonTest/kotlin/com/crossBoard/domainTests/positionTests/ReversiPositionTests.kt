package com.crossBoard.domainTests.positionTests

import com.crossBoard.domain.*
import com.crossBoard.domain.board.ReversiBoard
import com.crossBoard.domain.position.ReversiPosition
import kotlin.test.*

class ReversiPositionTests {

    private val boardDim = ReversiBoard.BOARD_DIM

    @Test
    fun `creates ReversiPosition with correct player and square`() {
        val square = "3b".toSquare(boardDim)
        val position = ReversiPosition(Player.BLACK, square)

        assertEquals(Player.BLACK, position.player)
        assertEquals(square, position.square)
    }

    @Test
    fun `toString returns expected format`() {
        val position = ReversiPosition(Player.WHITE, "6d".toSquare(boardDim))
        assertEquals("WHITE,6d", position.toString())
    }

    @Test
    fun `equals returns true for identical positions`() {
        val square = "4c".toSquare(boardDim)
        val pos1 = ReversiPosition(Player.WHITE, square)
        val pos2 = ReversiPosition(Player.WHITE, square)

        assertEquals(pos1, pos2)
    }

    @Test
    fun `equals returns false for different players`() {
        val square = "4c".toSquare(boardDim)
        val pos1 = ReversiPosition(Player.WHITE, square)
        val pos2 = ReversiPosition(Player.BLACK, square)

        assertNotEquals(pos1, pos2)
    }

    @Test
    fun `equals returns false for different squares`() {
        val pos1 = ReversiPosition(Player.WHITE, "4c".toSquare(boardDim))
        val pos2 = ReversiPosition(Player.WHITE, "5c".toSquare(boardDim))

        assertNotEquals(pos1, pos2)
    }

    @Test
    fun `hashCode is consistent for equal objects`() {
        val square = "7a".toSquare(boardDim)
        val pos1 = ReversiPosition(Player.BLACK, square)
        val pos2 = ReversiPosition(Player.BLACK, square)

        assertEquals(pos1.hashCode(), pos2.hashCode())
    }
}
