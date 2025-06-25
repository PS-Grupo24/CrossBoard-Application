import com.crossBoard.domain.Player
import com.crossBoard.domain.board.TicTacToeBoardRun
import com.crossBoard.domain.board.initialTicTacToePositions
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.MultiPlayerMatch

import kotlin.test.Test
import kotlin.test.assertEquals

class MatchTests {

    @Test fun gameTypeTests() {
        val gametype = MatchType.TicTacToe

        assertEquals("TicTacToe", gametype.name)
    }

    /*@Test fun multiplayermatchStartGameTest() {

        val player = Player.random()
        val board = TicTacToeBoardRun(
            initialTicTacToePositions(),
            emptyList(),
            Player.random(),
            player,
            player.other()
        )
        val newMatch = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)

        assertEquals(board, newMatch.board)
        assertEquals(1, newMatch.player1)
        assertEquals(null, newMatch.player2)
        assertEquals(MatchType.TicTacToe, newMatch.matchType)
    }
     */
}