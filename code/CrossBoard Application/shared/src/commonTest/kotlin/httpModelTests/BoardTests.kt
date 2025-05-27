package httpModelTests

import com.crossBoard.domain.*
import com.crossBoard.httpModel.BoardOutput
import com.crossBoard.httpModel.toBoard
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BoardTests {

    @Test fun boardOutputCreation() {
        val winner = "BLACK"
        val turn = "WHITE"
        val positions = listOf<String>("BLACK,1a", "BLACK,1b", "BLACK,1c", "WHITE,2a", "WHITE,2b", "EMPTY,2c", "EMPTY,3a", "EMPTY,3b", "EMPTY,3c")
        val moves = listOf<String>("BLACK,1a", "WHITE,2a", "BLACK,1b", "WHITE,2b", "BLACK,1c")

        val boardOutput = BoardOutput(winner, turn, positions, moves)

        assertEquals(winner, boardOutput.winner)
        assertEquals(turn, boardOutput.turn)
        assertEquals(positions, boardOutput.positions)
        assertEquals(moves, boardOutput.moves)
    }

    @Test fun toBoardTest() {
        val winner = null
        val winner2 = "BLACK"
        val turn = "WHITE"
        val positions = listOf<String>("BLACK,1a", "BLACK,1b", "BLACK,1c", "WHITE,2a", "WHITE,2b", "EMPTY,2c", "EMPTY,3a", "EMPTY,3b", "EMPTY,3c")
        val moves = listOf<String>("BLACK,1a", "WHITE,2a", "BLACK,1b", "WHITE,2b", "BLACK,1c")
        val movesResult = listOf<TicTacToeMove>(
            TicTacToeMove(Player.BLACK, Square(Row(2, 3), Column('a'))),
            TicTacToeMove(Player.WHITE, Square(Row(1, 3), Column('a'))),
            TicTacToeMove(Player.BLACK, Square(Row(2, 3), Column('b'))),
            TicTacToeMove(Player.WHITE, Square(Row(1, 3), Column('b'))),
            TicTacToeMove(Player.BLACK, Square(Row(2, 3), Column('c')))
        )

        val boardOutput = BoardOutput(winner, turn, positions, moves)

        val board1 = boardOutput.toBoard("tic", "BLACK", "RUNNING")

        assertTrue(board1 is TicTacToeBoardRun)
        assertEquals(positions, board1.positions.map { it.toString() })
        assertEquals(movesResult, board1.moves)

        assertEquals("BLACK", board1.player1.toString())
        assertEquals("WHITE", board1.player2.toString())
        assertEquals("WHITE", board1.turn.toString())

        val board2 = boardOutput.toBoard("tic", "BLACK", "WAITING")

        assertTrue(board2 is TicTacToeBoardRun)
        assertEquals(positions, board2.positions.map { it.toString() })
        assertEquals(movesResult, board2.moves)
        assertEquals("BLACK", board2.player1.toString())
        assertEquals("WHITE", board2.player2.toString())
        assertEquals("WHITE", board2.turn.toString())

        val board3 = boardOutput.toBoard("tic", "BLACK", "DRAW")

        assertTrue(board3 is TicTacToeBoardDraw)
        assertEquals(positions, board3.positions.map { it.toString() })
        assertEquals(movesResult, board3.moves)
        assertEquals("BLACK", board3.player1.toString())
        assertEquals("WHITE", board3.player2.toString())
        assertEquals("WHITE", board3.turn.toString())

        val boardOutput2 = BoardOutput(winner2, turn, positions, moves)
        val board4 = boardOutput2.toBoard("tic", "BLACK", "WIN")

        assertTrue(board4 is TicTacToeBoardWin)
        assertEquals(positions, board4.positions.map { it.toString() })
        assertEquals(movesResult, board4.moves)
        assertEquals("BLACK", board4.player1.toString())
        assertEquals("WHITE", board4.player2.toString())
        assertEquals("WHITE", board4.turn.toString())
    }

    @Test fun toBoardTestInvalid() {
        val winner = null
        val turn = "WHITE"
        val positions = listOf<String>("BLACK,1a", "BLACK,1b", "BLACK,1c", "WHITE,2a", "WHITE,2b", "EMPTY,2c", "EMPTY,3a", "EMPTY,3b", "EMPTY,3c")
        val moves = listOf<String>("BLACK,1a", "WHITE,2a", "BLACK,1b", "WHITE,2b", "BLACK,1c")
        val movesResult = listOf<TicTacToeMove>(
            TicTacToeMove(Player.BLACK, Square(Row(2, 3), Column('a'))),
            TicTacToeMove(Player.WHITE, Square(Row(1, 3), Column('a'))),
            TicTacToeMove(Player.BLACK, Square(Row(2, 3), Column('b'))),
            TicTacToeMove(Player.WHITE, Square(Row(1, 3), Column('b'))),
            TicTacToeMove(Player.BLACK, Square(Row(2, 3), Column('c')))
        )

        val boardOutput = BoardOutput(winner, turn, positions, moves)

        assertFailsWith<IllegalArgumentException> {
            boardOutput.toBoard("tic", "BLACK", "WIN")
        }
    }
}