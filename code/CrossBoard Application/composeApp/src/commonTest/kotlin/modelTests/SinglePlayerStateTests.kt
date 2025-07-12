package modelTests

import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.board.TicTacToeBoardRun
import com.crossBoard.domain.board.initialTicTacToePositions
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.model.SinglePlayerMatch
import com.crossBoard.model.SinglePlayerState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SinglePlayerStateTests {

    @Test fun singlePlayerStateTest() {

        val result = SinglePlayerState()

        assertNull(result.match)
        assertNull(result.player)
        assertEquals("", result.matchTypeInput)
        assertNull(result.errorMessage)

        val board = TicTacToeBoardRun(initialTicTacToePositions(), emptyList<TicTacToeMove>(), Player.BLACK, Player.WHITE, Player.BLACK)

        val singlePlayerMatch = SinglePlayerMatch(
            id = 1,
            board = board,
            state = MatchState.RUNNING,
            matchType = MatchType.TicTacToe,
            version = 1
        )

        val result2 = SinglePlayerState(
            match = singlePlayerMatch,
            player = Player.BLACK,
            matchTypeInput = "Test Type",
            errorMessage = "Test Error"
        )

        assertEquals(singlePlayerMatch, result2.match)
        assertEquals(Player.BLACK, result2.player)
        assertEquals("Test Type", result2.matchTypeInput)
        assertEquals("Test Error", result2.errorMessage)
    }
}