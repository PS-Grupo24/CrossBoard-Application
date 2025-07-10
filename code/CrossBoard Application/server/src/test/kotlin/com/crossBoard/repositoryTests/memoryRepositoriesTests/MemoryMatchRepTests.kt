package com.crossBoard.repositoryTests.memoryRepositoriesTests

import com.crossBoard.domain.Column
import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.MultiPlayerMatch
import com.crossBoard.domain.Row
import com.crossBoard.domain.Square
import com.crossBoard.domain.board.TicTacToeBoard
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.repository.memoryRepositories.MemoryMatchRep
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class MemoryMatchRepTests {

    private lateinit var matchRepo: MemoryMatchRep

    @BeforeEach
    fun setup() {
        matchRepo = MemoryMatchRep()
    }

    @Test
    fun `Test addMatch and getMatchById`() {
        val match = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)
        val matchId = matchRepo.addMatch(match)

        val foundMatch = matchRepo.getMatchById(matchId)
        assertNotNull(foundMatch)
        assertEquals(match, foundMatch)
    }

    @Test
    fun `Test getRunningMatchByUser`() {
        val match = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)
        matchRepo.addMatch(match)

        val foundMatch = matchRepo.getRunningMatchByUser(1)
        assertNotNull(foundMatch)
        assertEquals(match, foundMatch)
    }

    @Test
    fun `Test getWaitingMatch before and after adding a match`() {
        val matchType = MatchType.Reversi

        val foundBefore = matchRepo.getWaitingMatch(matchType)
        assertNull(foundBefore)

        val match = MultiPlayerMatch.startGame(1, matchType)
        matchRepo.addMatch(match)

        val foundAfter = matchRepo.getWaitingMatch(matchType)
        assertNotNull(foundAfter)

        assertNull(matchRepo.getWaitingMatch(MatchType.TicTacToe))
    }

    @Test
    fun `Test updateMatch`() {
        val match = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)
        val matchId = matchRepo.addMatch(match)

        val newMatch = match.play(
            TicTacToeMove(
                match.board.turn,
                Square(Row(2, TicTacToeBoard.BOARD_DIM), Column('a' + 0))
            )
        )

        val updatedMatch = matchRepo.updateMatch(
            newMatch.id,
            newMatch.board,
            newMatch.user1,
            newMatch.user2,
            newMatch.matchType,
            newMatch.version,
            newMatch.state,
            newMatch.winner
        )

        assertEquals(newMatch, updatedMatch)
        assertEquals(matchRepo.getMatchById(matchId), updatedMatch)
    }

    @Test
    fun `Test matchCancel`() {
        val match = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)
        val matchId = matchRepo.addMatch(match)

        val canceled = matchRepo.cancelSearch(1, matchId)
        assertEquals(matchId, canceled.matchId)

        assertNull(matchRepo.getMatchById(matchId))
    }

    @Test
    fun `Test getStatistics`() {
        val statsBefore = matchRepo.getStatistics(1)
        val type = MatchType.TicTacToe

        assertEquals(MatchType.entries.size, statsBefore.size)
        assertEquals(0, statsBefore.find { it.matchType == type.toString() }?.numberOfMatches)

        val match = MultiPlayerMatch.startGame(1, type)
        matchRepo.addMatch(match)

        val statsAfterAdding = matchRepo.getStatistics(1)
        assertEquals(0, statsAfterAdding.find { it.matchType == type.toString() }?.numberOfMatches)

        matchRepo.updateMatch(
            match.id,
            match.board.forfeit(match.getPlayerType(1)),
            match.user1,
            match.user2,
            match.matchType,
            match.version,
            MatchState.WIN,
            match.version + 1
        )

        val statsAfterEnded = matchRepo.getStatistics(1)
        assertEquals(1, statsAfterEnded.find { it.matchType == type.toString() }?.numberOfMatches)
    }
}
