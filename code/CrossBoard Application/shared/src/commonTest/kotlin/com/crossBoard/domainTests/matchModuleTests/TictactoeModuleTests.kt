package com.crossBoard.domainTests.matchModuleTests

import com.crossBoard.domain.*
import com.crossBoard.domain.board.TicTacToeBoard
import com.crossBoard.domain.board.TicTacToeBoardRun
import com.crossBoard.domain.matchModule.TicTacToeModule
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.domain.position.TicPosition
import com.crossBoard.httpModel.BoardOutput
import com.crossBoard.httpModel.moveHttp.TicTacToeMoveHttp
import com.crossBoard.httpModel.moveHttp.toMoveHttp
import com.crossBoard.httpModel.toBoard
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class TictactoeModuleTests {

    private val module = TicTacToeModule()

    @Test
    fun `matchType should be TicTacToe`() {
        assertEquals(MatchType.TicTacToe, module.matchType)
    }

    @Test
    fun `moveInputToMove should convert valid input to TicTacToeMove`() {
        val input = TicTacToeMoveHttp("BLACK", "1a")
        val move = module.moveHttpToMove(input)

        assertIs<TicTacToeMove>(move)
        assertEquals(Player.BLACK, move.player)
        assertEquals('a', move.square.column.symbol)
        assertEquals(move.square.row.boardDim - move.square.row.number, move.square.row.index)
    }

    @Test
    fun `moveToMoveOutput should convert move correctly`() {
        val move = TicTacToeMove(Player.WHITE, Square(Row(2, 3), Column('b')))
        val output = module.moveToMoveHttp(move)

        assertEquals("WHITE", output.player)
        assertEquals("1b", output.square)
    }

    @Test
    fun `getInitialBoard should return a valid board`() {
        val board = module.getInitialBoard()

        assertIs<TicTacToeBoard>(board)
        assertTrue(board.positions.size <= 9)
    }

    @Test
    fun `stringToPosition should convert properly`() {
        val str = "WHITE,3b"
        val pos = module.stringToPosition(str)

        assertIs<TicPosition>(pos)
        assertEquals(Player.WHITE, pos.player)
        assertEquals(3, pos.square.row.number)
        assertEquals('b', pos.square.column.symbol)
    }

    @Test
    fun `boardOutputToBoard should convert to TicTacToeBoardRun`() {
        val output = BoardOutput(
            turn = "BLACK",
            player1 = "BLACK",
            player2 = "WHITE",
            positions = listOf("BLACK,1a"),
            moves = listOf(TicTacToeMove(Player.BLACK, module.getSquare(1,1)).toMoveHttp(MatchType.TicTacToe)),
            winner = null
        )

        val board = output.toBoard(MatchType.TicTacToe.name, MatchState.RUNNING.name)

        assertIs<TicTacToeBoardRun>(board)
        assertEquals(Player.BLACK, board.turn)
        assertEquals(1, board.positions.size)
        assertEquals(1, board.moves.size)
    }

    @Test
    fun `getSquare should return correct Square`() {
        val square = module.getSquare(2, 0)

        assertEquals(2, square.row.index)
        assertEquals('a', square.column.symbol)
    }

    @Test
    fun `getMoveInput should construct valid input`() {
        val input = module.moveToMoveHttp(TicTacToeMove(Player.WHITE, module.getSquare(2, 3)))

        assertEquals("WHITE", input.player)
        assertEquals("1d", input.square)
    }

    @Test
    fun `moveOutputToMove should correctly reconstruct move`() {
        val output = TicTacToeMoveHttp("BLACK", "3b")
        val move = module.moveHttpToMove(output)

        assertIs<TicTacToeMove>(move)
        assertEquals(Player.BLACK, move.player)
        assertEquals(3, move.square.row.number)
        assertEquals('b', move.square.column.symbol)
    }
}
