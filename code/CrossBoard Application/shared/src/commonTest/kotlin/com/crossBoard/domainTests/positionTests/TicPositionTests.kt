package com.crossBoard.domainTests.positionTests
import com.crossBoard.domain.*
import com.crossBoard.domain.board.TicTacToeBoard
import com.crossBoard.domain.position.TicPosition
import kotlin.test.*

class TicPositionTests {

    private val boardDim = TicTacToeBoard.BOARD_DIM

    @Test
    fun `creates TicPosition with correct player and square`() {
        val square = "1a".toSquare(boardDim)
        val position = TicPosition(Player.WHITE, square)

        assertEquals(Player.WHITE, position.player)
        assertEquals(square, position.square)
    }

    @Test
    fun `toString returns expected format`() {
        val position = TicPosition(Player.BLACK, "2b".toSquare(boardDim))
        assertEquals("BLACK,2b", position.toString())
    }

    @Test
    fun `equals returns true for identical positions`() {
        val square = "3c".toSquare(boardDim)
        val pos1 = TicPosition(Player.WHITE, square)
        val pos2 = TicPosition(Player.WHITE, square)

        assertEquals(pos1, pos2)
    }

    @Test
    fun `equals returns false for different players`() {
        val square = "1a".toSquare(boardDim)
        val pos1 = TicPosition(Player.WHITE, square)
        val pos2 = TicPosition(Player.BLACK, square)

        assertNotEquals(pos1, pos2)
    }

    @Test
    fun `equals returns false for different squares`() {
        val pos1 = TicPosition(Player.WHITE, "1a".toSquare(boardDim))
        val pos2 = TicPosition(Player.WHITE, "1b".toSquare(boardDim))

        assertNotEquals(pos1, pos2)
    }

    @Test
    fun `hashCode is consistent for equal objects`() {
        val square = "2c".toSquare(boardDim)
        val pos1 = TicPosition(Player.BLACK, square)
        val pos2 = TicPosition(Player.BLACK, square)

        assertEquals(pos1.hashCode(), pos2.hashCode())
    }
}
