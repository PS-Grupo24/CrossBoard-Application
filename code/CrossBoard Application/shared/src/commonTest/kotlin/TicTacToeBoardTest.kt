import com.crossBoard.domain.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TicTacToeBoardTest {
    @Test fun creatingTicTacToeBoardRun() {

        val positions = emptyList<TicPosition>()
        val moves = emptyList<TicTacToeMove>()
        val player1 = Player.BLACK
        val player2 = Player.WHITE

        val board = TicTacToeBoardRun(positions, moves, player1, player1, player2)

        assertTrue(board is TicTacToeBoard)
        assertTrue(board is BoardRun)

        assertEquals(moves, board.moves)
        assertEquals(positions, board.positions)
        assertEquals(player1, board.player1)
        assertEquals(player2, board.player2)
        assertEquals(player1, board.turn)
    }

    @Test fun playingTicTacToeBoardRunWithoutSuccess() {

        val positions = initialTicTacToePositions()
        val moves = emptyList<TicTacToeMove>()
        val player1 = Player.BLACK
        val player2 = Player.WHITE

        val board = TicTacToeBoardRun(positions, moves, player1, player1, player2)

        assertTrue(board is TicTacToeBoard)
        assertTrue(board is BoardRun)

        assertFailsWith<IllegalArgumentException> {
            val move = TicTacToeMove(player2, Square(Row(0, 3), Column('a')))
            board.play(move)
        }

        assertFailsWith<IllegalArgumentException> {
            val move1 = TicTacToeMove(player1, Square(Row(0, 3), Column('a')))
            val newBoard = board.play(move1)
            val move2 = TicTacToeMove(player2, Square(Row(0, 3), Column('a')))
            newBoard.play(move2)
        }
    }

    @Test fun playingTicTacToeBoardRunWithSuccess() {
        val positions = initialTicTacToePositions()
        val moves = emptyList<TicTacToeMove>()
        val player1 = Player.BLACK
        val player2 = Player.WHITE

        val board = TicTacToeBoardRun(positions, moves, player1, player1, player2)

        assertTrue(board is TicTacToeBoard)
        assertTrue(board is BoardRun)

        val move = TicTacToeMove(player1, Square(Row(0, 3), Column('a')))
        val finalBoard = board.play(move)

        assertTrue(finalBoard is TicTacToeBoardRun)
        assertTrue(finalBoard is BoardRun)

        val finalPositions = listOf<TicPosition>(
            TicPosition(player1, Square(Row(0, 3), Column('a'))),
            TicPosition(Player.EMPTY, Square(Row(0, 3), Column('b'))),
            TicPosition(Player.EMPTY, Square(Row(0, 3), Column('c'))),
            TicPosition(Player.EMPTY, Square(Row(1, 3), Column('a'))),
            TicPosition(Player.EMPTY, Square(Row(1, 3), Column('b'))),
            TicPosition(Player.EMPTY, Square(Row(1, 3), Column('c'))),
            TicPosition(Player.EMPTY, Square(Row(2, 3), Column('a'))),
            TicPosition(Player.EMPTY, Square(Row(2, 3), Column('b'))),
            TicPosition(Player.EMPTY, Square(Row(2, 3), Column('c')))
        )

        finalPositions.forEachIndexed { index, ticPosition ->
            assertEquals(ticPosition.player, finalBoard.positions[index].player)
            assertEquals(ticPosition.square.row.number, finalBoard.positions[index].square.row.number)
            assertEquals(ticPosition.square.column.symbol, finalBoard.positions[index].square.column.symbol)
        }

        val finalMoves = listOf<TicTacToeMove>(
            TicTacToeMove(player1, Square(Row(0, 3), Column('a')))
        )

        assertEquals(finalMoves, finalBoard.moves)

        assertEquals(player2, finalBoard.turn)
        assertEquals(player1, finalBoard.player1)
        assertEquals(player2, finalBoard.player2)
    }

    @Test fun playingTicTacToeBoardRunToWin() {
        val positions = initialTicTacToePositions()
        val moves = emptyList<TicTacToeMove>()
        val player1 = Player.BLACK
        val player2 = Player.WHITE

        val board = TicTacToeBoardRun(positions, moves, player1, player1, player2)

        assertTrue(board is TicTacToeBoard)
        assertTrue(board is BoardRun)

        val move1 = TicTacToeMove(player1, Square(Row(0, 3), Column('a')))
        val board1 = board.play(move1)
        val move2 = TicTacToeMove(player2, Square(Row(1, 3), Column('a')))
        val board2 = board1.play(move2)
        val move3 = TicTacToeMove(player1, Square(Row(0, 3), Column('b')))
        val board3 = board2.play(move3)
        val move4 = TicTacToeMove(player2, Square(Row(1, 3), Column('b')))
        val board4 = board3.play(move4)
        val move5 = TicTacToeMove(player1, Square(Row(0, 3), Column('c')))
        val finalBoard = board4.play(move5)

        assertTrue(finalBoard is TicTacToeBoardWin)
        assertTrue(finalBoard is BoardWin)

        val finalPositions: List<TicPosition> = listOf<TicPosition>(
            TicPosition(player1, Square(Row(0, 3), Column('a'))),
            TicPosition(player1, Square(Row(0, 3), Column('b'))), TicPosition(player1, Square(Row(0, 3), Column('c'))),
            TicPosition(player2, Square(Row(1, 3), Column('a'))), TicPosition(player2, Square(Row(1, 3), Column('b'))),
            TicPosition(Player.EMPTY, Square(Row(1, 3), Column('c'))), TicPosition(Player.EMPTY, Square(Row(2, 3), Column('a'))),
            TicPosition(Player.EMPTY, Square(Row(2, 3), Column('b'))), TicPosition(Player.EMPTY, Square(Row(2, 3), Column('c')))
        )

        finalPositions.forEachIndexed { index, ticPosition ->
            assertEquals(ticPosition.player, finalBoard.positions[index].player)
            assertEquals(ticPosition.square.row.number, finalBoard.positions[index].square.row.number)
            assertEquals(ticPosition.square.column.symbol, finalBoard.positions[index].square.column.symbol)
        }

        val finalMoves = listOf<TicTacToeMove>(
            TicTacToeMove(player1, Square(Row(0, 3), Column('a'))),
            TicTacToeMove(player2, Square(Row(1, 3), Column('a'))),
            TicTacToeMove(player1, Square(Row(0, 3), Column('b'))),
            TicTacToeMove(player2, Square(Row(1, 3), Column('b'))),
            TicTacToeMove(player1, Square(Row(0, 3), Column('c')))
        )

        assertEquals(finalMoves, finalBoard.moves)

        assertEquals(player1, finalBoard.winner)
        assertEquals(player2, finalBoard.turn)
        assertEquals(player1, finalBoard.player1)
        assertEquals(player2, finalBoard.player2)
    }

    @Test fun playingTicTacToeBoardRunToDraw() {
        val positions = initialTicTacToePositions()
        val moves = emptyList<TicTacToeMove>()
        val player1 = Player.BLACK
        val player2 = Player.WHITE

        val board = TicTacToeBoardRun(positions, moves, player1, player1, player2)

        assertTrue(board is TicTacToeBoard)
        assertTrue(board is BoardRun)

        val move1 = TicTacToeMove(player1, Square(Row(0, 3), Column('a')))
        val board1 = board.play(move1)
        val move2 = TicTacToeMove(player2, Square(Row(1, 3), Column('a')))
        val board2 = board1.play(move2)
        val move3 = TicTacToeMove(player1, Square(Row(0, 3), Column('b')))
        val board3 = board2.play(move3)
        val move4 = TicTacToeMove(player2, Square(Row(1, 3), Column('b')))
        val board4 = board3.play(move4)
        val move5 = TicTacToeMove(player1, Square(Row(1, 3), Column('c')))
        val board5 = board4.play(move5)
        val move6 = TicTacToeMove(player2, Square(Row(0, 3), Column('c')))
        val board6 = board5.play(move6)
        val move7 = TicTacToeMove(player1, Square(Row(2, 3), Column('a')))
        val board7 = board6.play(move7)
        val move8 = TicTacToeMove(player2, Square(Row(2, 3), Column('b')))
        val board8 = board7.play(move8)
        val move9 = TicTacToeMove(player1, Square(Row(2, 3), Column('c')))
        val finalBoard = board8.play(move9)

        assertTrue(finalBoard is TicTacToeBoardDraw)
        assertTrue(finalBoard is BoardDraw)

        val finalPositions: List<TicPosition> = listOf<TicPosition>(
            TicPosition(player1, Square(Row(0, 3), Column('a'))),
            TicPosition(player1, Square(Row(0, 3), Column('b'))), TicPosition(player2, Square(Row(0, 3), Column('c'))),
            TicPosition(player2, Square(Row(1, 3), Column('a'))), TicPosition(player2, Square(Row(1, 3), Column('b'))),
            TicPosition(player1, Square(Row(1, 3), Column('c'))), TicPosition(player1, Square(Row(2, 3), Column('a'))),
            TicPosition(player2, Square(Row(2, 3), Column('b'))), TicPosition(player1, Square(Row(2, 3), Column('c')))
        )

        finalPositions.forEachIndexed { index, ticPosition ->
            assertEquals(ticPosition.player, finalBoard.positions[index].player)
            assertEquals(ticPosition.square.row.number, finalBoard.positions[index].square.row.number)
            assertEquals(ticPosition.square.column.symbol, finalBoard.positions[index].square.column.symbol)
        }

        val finalMoves = listOf<TicTacToeMove>(
            TicTacToeMove(player1, Square(Row(0, 3), Column('a'))),
            TicTacToeMove(player2, Square(Row(1, 3), Column('a'))),
            TicTacToeMove(player1, Square(Row(0, 3), Column('b'))),
            TicTacToeMove(player2, Square(Row(1, 3), Column('b'))),
            TicTacToeMove(player1, Square(Row(1, 3), Column('c'))),
            TicTacToeMove(player2, Square(Row(0, 3), Column('c'))),
            TicTacToeMove(player1, Square(Row(2, 3), Column('a'))),
            TicTacToeMove(player2, Square(Row(2, 3), Column('b'))),
            TicTacToeMove(player1, Square(Row(2, 3), Column('c')))
        )

        assertEquals(finalMoves, finalBoard.moves)

        assertEquals(player2, finalBoard.turn)
        assertEquals(player1, finalBoard.player1)
        assertEquals(player2, finalBoard.player2)
    }

    @Test fun forfeitingTicTacToeBoardRun() {
        val positions = initialTicTacToePositions()
        val moves = emptyList<TicTacToeMove>()
        val player1 = Player.BLACK
        val player2 = Player.WHITE

        val board = TicTacToeBoardRun(positions, moves, player1, player1, player2)

        assertTrue(board is TicTacToeBoard)
        assertTrue(board is BoardRun)

        val move = TicTacToeMove(player1, Square(Row(0, 3), Column('a')))
        val newBoard = board.play(move)

        assertTrue(newBoard is TicTacToeBoardRun)
        assertTrue(newBoard is BoardRun)

        val finalBoard = newBoard.forfeit(player1)

        assertTrue(finalBoard is TicTacToeBoardWin)
        assertTrue(finalBoard is BoardWin)

        assertEquals(player2, finalBoard.winner)
    }

    @Test fun verifyWinnerTicTacToeBoardWin() {
        val positions = initialTicTacToePositions()
        val moves = emptyList<TicTacToeMove>()
        val player1 = Player.BLACK
        val player2 = Player.WHITE

        val board = TicTacToeBoardRun(positions, moves, player1, player1, player2)

        assertTrue(board is TicTacToeBoard)
        assertTrue(board is BoardRun)

        val move1 = TicTacToeMove(player1, Square(Row(0, 3), Column('a')))
        val board1 = board.play(move1)
        val move2 = TicTacToeMove(player2, Square(Row(1, 3), Column('a')))
        val board2 = board1.play(move2)
        val move3 = TicTacToeMove(player1, Square(Row(0, 3), Column('b')))
        val board3 = board2.play(move3)
        val move4 = TicTacToeMove(player2, Square(Row(1, 3), Column('b')))
        val board4 = board3.play(move4)
        val move5 = TicTacToeMove(player1, Square(Row(0, 3), Column('c')))
        val finalBoard1 = board4.play(move5)

        assertTrue(finalBoard1 is TicTacToeBoardWin)
        assertTrue(finalBoard1 is BoardWin)
        assertEquals(player1, finalBoard1.winner)

        val move6 = TicTacToeMove(player1, Square(Row(0, 3), Column('a')))
        val board6 = board.play(move6)
        val move7 = TicTacToeMove(player2, Square(Row(0, 3), Column('b')))
        val board7 = board6.play(move7)
        val move8 = TicTacToeMove(player1, Square(Row(1, 3), Column('a')))
        val board8 = board7.play(move8)
        val move9 = TicTacToeMove(player2, Square(Row(1, 3), Column('b')))
        val board9 = board8.play(move9)
        val move10 = TicTacToeMove(player1, Square(Row(2, 3), Column('a')))
        val finalBoard2 = board9.play(move10)

        assertTrue(finalBoard2 is TicTacToeBoardWin)
        assertTrue(finalBoard2 is BoardWin)
        assertEquals(player1, finalBoard2.winner)

        val move11 = TicTacToeMove(player1, Square(Row(0, 3), Column('a')))
        val board11 = board.play(move11)
        val move12 = TicTacToeMove(player2, Square(Row(0, 3), Column('b')))
        val board12 = board11.play(move12)
        val move13 = TicTacToeMove(player1, Square(Row(1, 3), Column('b')))
        val board13 = board12.play(move13)
        val move14 = TicTacToeMove(player2, Square(Row(1, 3), Column('c')))
        val board14 = board13.play(move14)
        val move15 = TicTacToeMove(player1, Square(Row(2, 3), Column('c')))
        val finalBoard3 = board14.play(move15)

        assertTrue(finalBoard3 is TicTacToeBoardWin)
        assertTrue(finalBoard3 is BoardWin)
        assertEquals(player1, finalBoard3.winner)
    }

    @Test fun createTicTacToeBoardWin() {
        val positions = initialTicTacToePositions()
        val moves = emptyList<TicTacToeMove>()
        val player1 = Player.BLACK
        val player2 = Player.WHITE

        val board = TicTacToeBoardWin(player1, positions, moves, player1, player1, player2)

        assertTrue(board is TicTacToeBoard)
        assertTrue(board is BoardWin)

        assertEquals(player1, board.winner)
        assertEquals(moves, board.moves)
        assertEquals(positions, board.positions)
        assertEquals(player1, board.player1)
        assertEquals(player2, board.player2)
        assertEquals(player1, board.turn)
    }

    @Test fun playTicTacToeBoardWin() {
        val positions = initialTicTacToePositions()
        val moves = emptyList<TicTacToeMove>()
        val player1 = Player.BLACK
        val player2 = Player.WHITE

        val board = TicTacToeBoardWin(player1, positions, moves, player1, player1, player2)

        assertTrue(board is TicTacToeBoard)
        assertTrue(board is BoardWin)

        assertFailsWith<IllegalStateException> {
            val move = TicTacToeMove(player2, Square(Row(0, 3), Column('a')))
            board.play(move)
        }
    }

    @Test fun forfeitTicTacToeBoardWin() {
        val positions = initialTicTacToePositions()
        val moves = emptyList<TicTacToeMove>()
        val player1 = Player.BLACK
        val player2 = Player.WHITE

        val board = TicTacToeBoardWin(player1, positions, moves, player1, player1, player2)

        assertTrue(board is TicTacToeBoard)
        assertTrue(board is BoardWin)

        assertFailsWith<IllegalStateException> {
            board.forfeit(player2)
        }
    }

    @Test fun createTicTacToeBoardDraw() {
        val positions = initialTicTacToePositions()
        val moves = emptyList<TicTacToeMove>()
        val player1 = Player.BLACK
        val player2 = Player.WHITE

        val board = TicTacToeBoardDraw(positions, moves, player1, player1, player2)

        assertTrue(board is TicTacToeBoard)
        assertTrue(board is BoardDraw)

        assertEquals(moves, board.moves)
        assertEquals(positions, board.positions)
        assertEquals(player1, board.player1)
        assertEquals(player2, board.player2)
        assertEquals(player1, board.turn)
    }

    @Test fun playTicTacToeBoardDraw() {
        val positions = initialTicTacToePositions()
        val moves = emptyList<TicTacToeMove>()
        val player1 = Player.BLACK
        val player2 = Player.WHITE

        val board = TicTacToeBoardDraw(positions, moves, player1, player1, player2)

        assertTrue(board is TicTacToeBoard)
        assertTrue(board is BoardDraw)

        assertFailsWith<IllegalStateException> {
            val move = TicTacToeMove(player1, Square(Row(0, 3), Column('a')))
            board.play(move)
        }
    }

    @Test fun forfeitTicTacToeBoardDraw() {
        val positions = initialTicTacToePositions()
        val moves = emptyList<TicTacToeMove>()
        val player1 = Player.BLACK
        val player2 = Player.WHITE

        val board = TicTacToeBoardDraw(positions, moves, player1, player1, player2)

        assertTrue(board is TicTacToeBoard)
        assertTrue(board is BoardDraw)

        assertFailsWith<IllegalStateException> {
            board.forfeit(player2)
        }
    }

    @Test fun initialTicTacToePositionsTest() {
        val positions = initialTicTacToePositions()

        assertEquals(9, positions.size)

        positions.forEachIndexed { index, position ->
            assertEquals(Player.EMPTY, position.player)
            assertEquals(Row(index / TicTacToeBoard.BOARD_DIM, TicTacToeBoard.BOARD_DIM).number, position.square.row.number)
            assertEquals(Column('a' + index % TicTacToeBoard.BOARD_DIM).symbol, position.square.column.symbol)
        }
    }
}