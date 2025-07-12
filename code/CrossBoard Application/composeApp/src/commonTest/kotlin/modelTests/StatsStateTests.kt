package modelTests

import com.crossBoard.httpModel.MatchStats
import com.crossBoard.model.StatsState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StatsStateTests {

    @Test fun statsStateTest() {
        val result = StatsState()

        assertEquals(emptyList(), result.stats)
        assertNull(result.errorMessage)

        val matchStats = MatchStats(
            matchType = "TicTacToe",
            numberOfMatches = 0,
            numberOfWins = 0,
            numberOfDraws = 0,
            numberOfLosses = 0,
            averageWinningRate = 0.0,
        )

        val result2 = StatsState(
            stats = listOf(matchStats),
            errorMessage = "Test Error"
        )

        assertEquals(listOf(matchStats), result2.stats)
        assertEquals("Test Error", result2.errorMessage)
    }
}