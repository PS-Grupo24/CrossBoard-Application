
import domain.Player
import domain.TicTacToeBoardRun
import domain.initialTicTacToePositions
import model.GameType
import model.MultiPlayerMatch
import repository.memoryRepositories.MemoryMatchRep
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MemoryMatchRepTests {
    @Test fun addMatchWithSuccess(){
        val matchRep = MemoryMatchRep()
        val player1 = Player.random()
        val board = TicTacToeBoardRun(
            initialTicTacToePositions(),
            emptyList(),
            Player.random(),
            player1,
            player1.other())

        val newMatch = matchRep.addMatch(board, 1U, 2U, GameType.TicTacToe)

        val matchResult = MultiPlayerMatch(board, 1U, 1U, 2U, GameType.TicTacToe)

        assertEquals(matchResult, newMatch)
        assertEquals(matchResult.board, newMatch.board)
        assertEquals(matchResult.id, newMatch.id)
        assertEquals(matchResult.player1, newMatch.player1)
        assertEquals(matchResult.player2, newMatch.player2)
        assertEquals(matchResult.gameType, newMatch.gameType)
    }

    @Test fun getRunningMatchByUserWithSuccess() {
        val matchRep = MemoryMatchRep()
        val player1 = Player.random()
        val board = TicTacToeBoardRun(
            initialTicTacToePositions(),
            emptyList(),
            Player.random(),
            player1,
            player1.other())

        val newMatch = matchRep.addMatch(board, 1U, 2U, GameType.TicTacToe)

        val runningMatch = matchRep.getRunningMatchByUser(1U)

        assertNotNull(runningMatch)

        assertEquals(newMatch, runningMatch)
        assertEquals(newMatch.board, runningMatch.board)
        assertEquals(newMatch.id, runningMatch.id)
        assertEquals(newMatch.player1, runningMatch.player1)
        assertEquals(newMatch.player2, runningMatch.player2)
        assertEquals(newMatch.gameType, runningMatch.gameType)

        val runningMatch2 = matchRep.getRunningMatchByUser(2U)

        assertNotNull(runningMatch2)

        assertEquals(newMatch, runningMatch2)
        assertEquals(newMatch.board, runningMatch2.board)
        assertEquals(newMatch.id, runningMatch2.id)
        assertEquals(newMatch.player1, runningMatch2.player1)
        assertEquals(newMatch.player2, runningMatch2.player2)
        assertEquals(newMatch.gameType, runningMatch2.gameType)
    }

    @Test fun getRunningMatchByUserWithoutSuccess() {
        val matchRep = MemoryMatchRep()

        val runningMatch = matchRep.getRunningMatchByUser(1U)

        assertNull(runningMatch)
    }

    @Test fun getMatchByIdWithSuccess() {
        val matchRep = MemoryMatchRep()
        val player1 = Player.random()
        val board = TicTacToeBoardRun(
            initialTicTacToePositions(),
            emptyList(),
            Player.random(),
            player1,
            player1.other())

        val newMatch = matchRep.addMatch(board, 1U, 2U, GameType.TicTacToe)

        val match = matchRep.getMatchById(1U)

        assertNotNull(match)

        assertEquals(newMatch, match)
        assertEquals(newMatch.board, match.board)
        assertEquals(newMatch.id, match.id)
        assertEquals(newMatch.player1, match.player1)
        assertEquals(newMatch.player2, match.player2)
        assertEquals(newMatch.gameType, match.gameType)
    }

    @Test fun getMatchByIdWithoutSuccess() {
        val matchRep = MemoryMatchRep()

        val match = matchRep.getMatchById(1U)

        assertNull(match)
    }

    @Test fun getWaitingMatchWithSuccess() {
        val matchRep = MemoryMatchRep()
        val player1 = Player.random()
        val board = TicTacToeBoardRun(
            initialTicTacToePositions(),
            emptyList(),
            Player.random(),
            player1,
            player1.other())

        val newMatch = matchRep.addMatch(board, 1U, null, GameType.TicTacToe)

        val waitingMatch = matchRep.getWaitingMatch(GameType.TicTacToe)

        assertNotNull(waitingMatch)

        assertEquals(newMatch, waitingMatch)
        assertEquals(newMatch.board, waitingMatch.board)
        assertEquals(newMatch.id, waitingMatch.id)
        assertEquals(newMatch.player1, waitingMatch.player1)
        assertEquals(newMatch.player2, waitingMatch.player2)
        assertEquals(newMatch.gameType, waitingMatch.gameType)
    }

    @Test fun getWaitingMatchWithoutSuccess() {
        val matchRep = MemoryMatchRep()

        val player1 = Player.random()
        val board = TicTacToeBoardRun(
            initialTicTacToePositions(),
            emptyList(),
            Player.random(),
            player1,
            player1.other())

        val waitingMatch = matchRep.getWaitingMatch(GameType.TicTacToe)

        assertNull(waitingMatch)

        matchRep.addMatch(board, 1U, 2U, GameType.TicTacToe)

        val waitingMatch2 = matchRep.getWaitingMatch(GameType.TicTacToe)

        assertNull(waitingMatch2)
    }

    @Test fun updateMatchWithSuccess() {
        val matchRep = MemoryMatchRep()

        val player1 = Player.random()
        val board = TicTacToeBoardRun(
            initialTicTacToePositions(),
            emptyList(),
            Player.random(),
            player1,
            player1.other())

        val newMatch = matchRep.addMatch(board, 1U, 2U, GameType.TicTacToe)

        val firstMatch = MultiPlayerMatch(board, 1U, 1U, 2U, GameType.TicTacToe)

        assertNotNull(firstMatch)

        assertEquals(newMatch, firstMatch)
        assertEquals(newMatch.board, firstMatch.board)
        assertEquals(newMatch.id, firstMatch.id)
        assertEquals(newMatch.player1, firstMatch.player1)
        assertEquals(newMatch.player2, firstMatch.player2)
        assertEquals(newMatch.gameType, firstMatch.gameType)

        val updatedMatch = matchRep.updateMatch(1U, board, 2U, 1U, GameType.TicTacToe)

        val secondMatch = MultiPlayerMatch(board, 1U, 2U, 1U, GameType.TicTacToe)

        assertNotNull(secondMatch)

        assertEquals(updatedMatch, secondMatch)
        assertEquals(updatedMatch.board, secondMatch.board)
        assertEquals(updatedMatch.id, secondMatch.id)
        assertEquals(updatedMatch.player1, secondMatch.player1)
        assertEquals(updatedMatch.player2, secondMatch.player2)
        assertEquals(updatedMatch.gameType, secondMatch.gameType)
    }
}