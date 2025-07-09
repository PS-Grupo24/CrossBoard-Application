package com.crossBoard.domainTests.matchModuleTests

import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.board.*
import com.crossBoard.domain.matchModule.ReversiModule
import com.crossBoard.domain.move.ReversiMove
import com.crossBoard.httpModel.BoardOutput
import com.crossBoard.httpModel.ReversiMoveInput
import com.crossBoard.httpModel.ReversiMoveOutput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertFailsWith

class ReversiModuleTest {
    private val module = ReversiModule()
    private val boardDim = 8

    @Test
    fun `matchType should be Reversi`() {
        assertEquals(MatchType.Reversi, module.matchType)
    }

    @Test
    fun `moveInputToMove should convert correctly`() {
        val input = ReversiMoveInput("BLACK", "3c")
        val move = module.moveInputToMove(input)
        assertEquals(Player.BLACK, move.player)
        assertEquals(5, move.square.row.index)
        assertEquals('c', move.square.column.symbol)
    }

    @Test
    fun `moveToMoveOutput should convert correctly`() {
        val move = ReversiMove(Player.WHITE, module.getSquare(3, 2))
        val output = module.moveToMoveOutput(move)

        assertEquals("WHITE", output.player)
        assertEquals("5c", output.square)
    }

    @Test
    fun `moveOutputToMove should convert correctly`() {
        val output = ReversiMoveOutput("WHITE", "4d")
        val move = module.moveOutputToMove(output)

        assertEquals(Player.WHITE, move.player)
        assertEquals(4, move.square.row.index)
        assertEquals('d', move.square.column.symbol)
    }

    @Test
    fun `moveToString and stringToMove should be reversible`() {
        val originalMove = ReversiMove(Player.BLACK, module.getSquare(4, 1))
        val str = module.moveToString(originalMove)
        val parsed = module.stringToMove(str)

        assertEquals(originalMove.player, parsed.player)
        assertEquals(originalMove.square, parsed.square)
    }

    @Test
    fun `getInitialBoard should return ReversiBoardRun`() {
        val board = module.getInitialBoard()
        assertIs<ReversiBoardRun>(board)
        assertEquals(boardDim * boardDim, board.positions.size)
    }

    @Test
    fun `stringToPosition should parse correctly`() {
        val input = "BLACK,3c"
        val pos = module.stringToPosition(input)

        assertEquals(Player.BLACK, pos.player)
        assertEquals(5, pos.square.row.index)
        assertEquals('c', pos.square.column.symbol)
    }

    @Test
    fun `getMoveInput should format input properly`() {
        val input = module.getMoveInput(Player.WHITE, 6, 'f')
        assertEquals("WHITE", input.player)
        assertEquals("6f", input.square)
    }

    @Test
    fun `boardOutputToBoard should return correct board by state`() {
        val boardOutput = BoardOutput(
            positions = listOf(module.stringToPosition("BLACK,4d").toString()),
            moves = listOf(module.stringToMove("WHITE,3d").toString()),
            turn = "BLACK",
            player1 = "BLACK",
            winner = "WHITE",
            player2 = "WHITE",
        )

        val runningBoard = module.boardOutputToBoard(boardOutput, "RUNNING")
        val winBoard = module.boardOutputToBoard(boardOutput, "WIN")
        val drawBoard = module.boardOutputToBoard(boardOutput.copy(winner = null), "DRAW")

        assertIs<ReversiBoardRun>(runningBoard)
        assertIs<ReversiBoardWin>(winBoard)
        assertIs<ReversiBoardDraw>(drawBoard)
    }

    @Test
    fun `boardOutputToBoard should throw if WIN without winner`() {
        val boardOutput = BoardOutput(
            positions = listOf(module.stringToPosition("BLACK,4d").toString()),
            moves = listOf(module.stringToMove("WHITE,3d").toString()),
            turn = "BLACK",
            player1 = "BLACK",
            winner = null,
            player2 = "WHITE",
        )

        assertFailsWith<IllegalArgumentException> {
            module.boardOutputToBoard(boardOutput, "WIN")
        }
    }
}