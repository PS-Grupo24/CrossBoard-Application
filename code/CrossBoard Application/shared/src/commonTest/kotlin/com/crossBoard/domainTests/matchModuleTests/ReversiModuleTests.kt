package com.crossBoard.domainTests.matchModuleTests

import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.board.*
import com.crossBoard.domain.matchModule.ReversiModule
import com.crossBoard.domain.move.ReversiMove
import com.crossBoard.httpModel.BoardOutput
import com.crossBoard.httpModel.moveInput.ReversiMoveInput
import com.crossBoard.httpModel.moveOutput.ReversiMoveOutput
import com.crossBoard.httpModel.toBoard
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
        val input = module.moveToMoveInput(ReversiMove(Player.WHITE, module.getSquare(6, 5)))
        assertEquals("WHITE", input.player)
        assertEquals("2f", input.square)
    }

    @Test
    fun `boardOutputToBoard should return correct board by state`() {
        val boardOutput = BoardOutput(
            positions = listOf("BLACK,4d"),
            moves = listOf(module.moveToMoveOutput(
                ReversiMove(
                    Player.WHITE,module.getSquare(5, 2),))),
            turn = "BLACK",
            player1 = "BLACK",
            winner = "WHITE",
            player2 = "WHITE",
        )

        val runningBoard = boardOutput.toBoard(MatchType.Reversi.name, MatchState.RUNNING.name )
        val winBoard = boardOutput.toBoard(MatchType.Reversi.name, MatchState.WIN.name)
        val drawBoard = boardOutput.toBoard(MatchType.Reversi.name, MatchState.DRAW.name)

        assertIs<ReversiBoardRun>(runningBoard)
        assertIs<ReversiBoardWin>(winBoard)
        assertIs<ReversiBoardDraw>(drawBoard)
    }

    @Test
    fun `boardOutputToBoard should throw if WIN without winner`() {
        val boardOutput = BoardOutput(
            positions = listOf(module.stringToPosition("BLACK,4d").toString()),
            moves = listOf(module.moveToMoveOutput(
                ReversiMove(
                    Player.WHITE,module.getSquare(5, 2),))),
            turn = "BLACK",
            player1 = "BLACK",
            winner = null,
            player2 = "WHITE",
        )

        assertFailsWith<IllegalArgumentException> {
            boardOutput.toBoard(MatchType.Reversi.name, MatchState.WIN.name)
        }
    }
}