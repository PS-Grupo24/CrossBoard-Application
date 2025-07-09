package com.crossBoard.domainTests.moveTests

import com.crossBoard.domain.board.TicTacToeBoard
import com.crossBoard.domain.*
import com.crossBoard.domain.move.TicTacToeMove
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TicTacToeMoveTests {

    private val boardDim = TicTacToeBoard.BOARD_DIM

    @Test
    fun `TicTacToeMove equality - same player and square`() {
        val player = Player.BLACK
        val square = "1a".toSquare(boardDim)

        val move1 = TicTacToeMove(player, square)
        val move2 = TicTacToeMove(player, square)

        assertEquals(move1, move2)
        assertEquals(move1.hashCode(), move2.hashCode())
    }

    @Test
    fun `TicTacToeMove inequality - different player`() {
        val square = "2b".toSquare(boardDim)

        val move1 = TicTacToeMove(Player.BLACK, square)
        val move2 = TicTacToeMove(Player.WHITE, square)

        assertNotEquals(move1, move2)
    }

    @Test
    fun `TicTacToeMove inequality - different square`() {
        val player = Player.WHITE

        val move1 = TicTacToeMove(player, "1a".toSquare(boardDim))
        val move2 = TicTacToeMove(player, "2b".toSquare(boardDim))

        assertNotEquals(move1, move2)
    }

    @Test
    fun `TicTacToeMove hashCode consistency`() {
        val move = TicTacToeMove(Player.WHITE, "3c".toSquare(boardDim))

        val hash1 = move.hashCode()
        val hash2 = move.hashCode()

        assertEquals(hash1, hash2)
    }
}
