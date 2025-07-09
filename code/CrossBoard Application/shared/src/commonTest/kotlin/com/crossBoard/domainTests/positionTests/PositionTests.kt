package com.crossBoard.domainTests.positionTests

import com.crossBoard.domain.*
import com.crossBoard.domain.board.ReversiBoard
import com.crossBoard.domain.board.TicTacToeBoard
import com.crossBoard.domain.position.toPosition
import com.crossBoard.domain.position.TicPosition
import com.crossBoard.domain.position.ReversiPosition
import kotlin.test.*

class PositionTests {

    private val ticDim = TicTacToeBoard.BOARD_DIM
    private val revDim = ReversiBoard.BOARD_DIM

    @Test
    fun `parse TicTacToe position string correctly`() {
        val input = "WHITE,1a"
        val position = input.toPosition(MatchType.TicTacToe)

        assertTrue(position is TicPosition)
        assertEquals(Player.WHITE, position.player)
        assertEquals("1a", position.square.toString())
    }

    @Test
    fun `parse Reversi position string correctly`() {
        val input = "BLACK,5d"
        val position = input.toPosition(MatchType.Reversi)

        assertTrue(position is ReversiPosition)
        assertEquals(Player.BLACK, position.player)
        assertEquals("5d", position.square.toString())
    }

    @Test
    fun `throws error on malformed string`() {
        val invalid = "INVALID_STRING"
        assertFailsWith<IllegalArgumentException> {
            invalid.toPosition(MatchType.TicTacToe)
        }
    }

    @Test
    fun `throws error on invalid player`() {
        val invalid = "GHOST,1a"
        assertFailsWith<IllegalArgumentException> {
            invalid.toPosition(MatchType.TicTacToe)
        }
    }

    @Test
    fun `throws error on invalid square`() {
        val invalid = "WHITE,9z"
        assertFailsWith<IllegalArgumentException> {
            invalid.toPosition(MatchType.TicTacToe)
        }
    }
}
