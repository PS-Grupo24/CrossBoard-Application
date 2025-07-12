package modelTests

import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.MultiPlayerMatch
import com.crossBoard.domain.Player
import com.crossBoard.domain.board.TicTacToeBoard
import com.crossBoard.domain.board.TicTacToeBoardRun
import com.crossBoard.domain.board.initialTicTacToePositions
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.model.MultiplayerMatchUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class MultiplayerMatchUiStateTests {

    @Test fun multiplayerMatchUiStateTests() {

        val result = MultiplayerMatchUiState()

        assertNull(result.currentMatch)
        assertEquals("", result.player1Username)
        assertEquals("", result.player2Username)
        assertEquals("", result.gameTypeInput)
        assertFalse(result.isLoading)
        assertNull(result.errorMessage)
        assertNull(result.timeLeftSeconds)

        val board = TicTacToeBoardRun(initialTicTacToePositions(), emptyList<TicTacToeMove>(), Player.BLACK, Player.WHITE, Player.BLACK)

        val multiplayerMatch = MultiPlayerMatch(
            board = board,
            id = 1,
            state = MatchState.RUNNING,
            user1 = 1,
            user2 = 2,
            matchType = MatchType.TicTacToe,
            version = 1,
            winner = null
        )

        val result2 = MultiplayerMatchUiState(
            currentMatch = multiplayerMatch,
            player1Username = "Player1",
            player2Username = "Player2",
            gameTypeInput = "TicTacToe",
            isLoading = true,
            errorMessage = "An error occurred",
            timeLeftSeconds = 120
        )

        assertEquals(multiplayerMatch, result2.currentMatch)
        assertEquals("Player1", result2.player1Username)
        assertEquals("Player2", result2.player2Username)
        assertEquals("TicTacToe", result2.gameTypeInput)
        assertEquals(true, result2.isLoading)
        assertEquals("An error occurred", result2.errorMessage)
        assertEquals(120, result2.timeLeftSeconds)
    }
}