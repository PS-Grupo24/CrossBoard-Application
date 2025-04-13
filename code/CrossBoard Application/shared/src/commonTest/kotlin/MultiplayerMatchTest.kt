import domain.GameType
import domain.MultiPlayerMatch
import domain.toGameType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MultiplayerMatchTest {

    @Test
    fun `Test Play`(){
        val userId1 = 1
        val userId2 = 2
        val gameType = "tic".toGameType()
        assertNotNull(gameType)
        val match = MultiPlayerMatch.startGame(userId1, gameType)

        val updatedMatch = match.join(userId2)

        assertEquals(match.version + 1, updatedMatch.version)
        assertEquals(match.board.positions, updatedMatch.board.positions)
    }
}