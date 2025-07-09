package com.crossBoard.domainTests.boardTests

import com.crossBoard.domain.*
import com.crossBoard.domain.board.*
import com.crossBoard.domain.move.ReversiMove
import com.crossBoard.domain.position.ReversiPosition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ReversiBoardTest {

    private val player1 = Player.BLACK
    private val player2 = Player.WHITE

    private fun createInitialBoard(): ReversiBoardRun {
        return ReversiBoardRun(
            initialReversiPositions(),
            moves = emptyList(),
            turn = player1,
            player1 = player1,
            player2 = player2
        )
    }

    @Test
    fun `initial board setup has 4 center pieces`() {
        val board = createInitialBoard()
        val positions = board.positions
        assertEquals(64, positions.size)

        val blackCount = positions.count { it.player == Player.BLACK }
        val whiteCount = positions.count { it.player == Player.WHITE }
        val emptyCount = positions.count { it.player == Player.EMPTY }

        assertEquals(2, blackCount)
        assertEquals(2, whiteCount)
        assertEquals(60, emptyCount)
    }

    @Test
    fun `play a valid opening move`() {
        val board = createInitialBoard()
        val square = Square(Row(2, 8), Column('d'))
        val move = ReversiMove(player1, square)
        val newBoard = board.play(move) as ReversiBoardRun

        assertEquals(player2, newBoard.turn)
        assertTrue(newBoard.moves.contains(move))
        assertEquals(Player.BLACK, newBoard.get(square))
    }

    @Test
    fun `playing invalid move on non-empty square should throw`() {
        val board = createInitialBoard()
        val occupiedSquare = Square(Row(3, 8), Column('d'))
        val move = ReversiMove(player1, occupiedSquare)

        assertFailsWith(IllegalArgumentException::class) {
            board.play(move)
        }
    }

    @Test
    fun `wrong player cannot play`() {
        val board = createInitialBoard()
        val validSquare = possibleMoves(player1, board.positions).first()
        val move = ReversiMove(player2, validSquare)

        assertFailsWith(IllegalArgumentException::class) {
            board.play(move)
        }
    }

    @Test
    fun `skip a turn when no valid moves`() {
        val blockedPositions = mutableListOf<ReversiPosition>()
        repeat(ReversiBoard.BOARD_DIM) { row ->
            repeat(ReversiBoard.BOARD_DIM) { col ->
                val square = Square(Row(row, ReversiBoard.BOARD_DIM), Column('a' + col))
                val player = Player.WHITE
                blockedPositions.add(ReversiPosition(player, square))
            }
        }

        val emptySquare = Square(Row(0, ReversiBoard.BOARD_DIM), Column('a'))
        blockedPositions[0] = ReversiPosition(Player.EMPTY, emptySquare)

        val board = ReversiBoardRun(
            positions = blockedPositions,
            moves = emptyList(),
            turn = player1,
            player1 = player1,
            player2 = player2
        )

        assertTrue(possibleMoves(player1, board.positions).isEmpty())

        val skipBoard = board.skip(player1)
        assertEquals(player2, skipBoard.turn)
    }


    @Test
    fun `cannot skip if there are valid moves`() {
        val board = createInitialBoard()
        assertFailsWith(IllegalArgumentException::class) {
            board.skip(player1)
        }
    }

    @Test
    fun `forfeit ends the game and assigns win to other player`() {
        val board = createInitialBoard()
        val forfeitedBoard = board.forfeit(player1)
        assertTrue(forfeitedBoard is ReversiBoardWin)
        assertEquals(player2, (forfeitedBoard).winner)
    }

    @Test
    fun `playing after game has ended throws exception`() {
        val board = ReversiBoardWin(
            winner = player1,
            positions = initialReversiPositions(),
            moves = emptyList(),
            turn = player2,
            player1 = player1,
            player2 = player2
        )
        val move = ReversiMove(player2, Square(Row(0, 8), Column('a')))
        assertFailsWith(IllegalStateException::class) {
            board.play(move)
        }
    }
}
