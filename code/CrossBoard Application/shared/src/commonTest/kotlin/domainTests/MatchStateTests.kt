package domainTests

import com.crossBoard.domain.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MatchStateTests {

    @Test fun matchStateValuesTest() {
        val waitingStatus = MatchState.WAITING

        assertEquals("WAITING", waitingStatus.toString())
        assertEquals(0, waitingStatus.ordinal)

        val runningStatus = MatchState.RUNNING

        assertEquals("RUNNING", runningStatus.toString())
        assertEquals(1, runningStatus.ordinal)

        val drawStatus = MatchState.DRAW

        assertEquals("DRAW", drawStatus.toString())
        assertEquals(2, drawStatus.ordinal)

        val winStatus = MatchState.WIN

        assertEquals("WIN", winStatus.toString())
        assertEquals(3, winStatus.ordinal)
    }

    @Test fun toMatchStateTest() {

        assertEquals(MatchState.WAITING, "WAITING".toMatchState())
        assertEquals(MatchState.RUNNING, "RUNNING".toMatchState())
        assertEquals(MatchState.DRAW, "DRAW".toMatchState())
        assertEquals(MatchState.WIN, "WIN".toMatchState())

        assertFailsWith<IllegalArgumentException> {
            "INVALID".toMatchState()
        }
    }

    @Test fun getMatchStateFromBoardTest() {
        val player1 = Player.random()

        val board = TicTacToeBoardRun(
            initialTicTacToePositions(),
            emptyList(),
            Player.random(),
            player1,
            player1.other(),
        )

        assertEquals(MatchState.WAITING, getMatchStateFromBoard(null, board))

        assertEquals(MatchState.RUNNING, getMatchStateFromBoard(2, board))

        val boardWin = TicTacToeBoardWin(
            player1,
            initialTicTacToePositions(),
            emptyList(),
            Player.random(),
            player1,
            player1.other(),
        )

        assertEquals(MatchState.WIN, getMatchStateFromBoard(2, boardWin))

        val boardDraw = TicTacToeBoardDraw(
            initialTicTacToePositions(),
            emptyList(),
            Player.random(),
            player1,
            player1.other(),
        )

        assertEquals(MatchState.DRAW, getMatchStateFromBoard(2, boardDraw))
    }
}