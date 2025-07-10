package com.crossBoard.serviceTests

import com.crossBoard.domain.Column
import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Row
import com.crossBoard.domain.Square
import com.crossBoard.domain.board.TicTacToeBoard
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.repository.memoryRepositories.MemoryMatchRep
import com.crossBoard.service.MatchService
import com.crossBoard.util.ApiError
import com.crossBoard.util.Failure
import com.crossBoard.util.Success
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
class MatchServiceTests {
    private lateinit var repo: MemoryMatchRep
    private lateinit var matchService: MatchService

    @BeforeEach
    fun setup() {
        repo = MemoryMatchRep()
        matchService = MatchService(repo)
    }

    @Test
    fun testEnterMatchCreatesNew() {
        val result = matchService.enterMatch(1, MatchType.TicTacToe)
        assertTrue(result is Success)
        assertEquals(1, (result as Success).value.user1)
        assertEquals(MatchState.WAITING, result.value.state)
    }

    @Test
    fun testEnterMatchJoinsExisting() {
        matchService.enterMatch(1, MatchType.TicTacToe)
        val result = matchService.enterMatch(2, MatchType.TicTacToe)
        assertTrue(result is Success)
        val match = (result as Success).value
        assertEquals(1, match.user1)
        assertEquals(2, match.user2)
        assertEquals(MatchState.RUNNING, match.state)
    }

    @Test
    fun testEnterMatchUserAlreadyInMatch() {
        matchService.enterMatch(1, MatchType.TicTacToe)
        matchService.enterMatch(2, MatchType.TicTacToe)
        val result = matchService.enterMatch(2, MatchType.TicTacToe)
        assertTrue(result is Failure)
        assertEquals(ApiError.USER_ALREADY_IN_MATCH, (result as Failure).value)
    }

    @Test
    fun testGetMatchByIdFound() {
        val created = (matchService.enterMatch(1, MatchType.TicTacToe) as Success).value
        val result = matchService.getMatchById(created.id)
        assertTrue(result is Success)
        assertEquals(created.id, (result as Success).value.id)
    }

    @Test
    fun testGetMatchByIdNotFound() {
        val result = matchService.getMatchById(999)
        assertTrue(result is Failure)
        assertEquals(ApiError.MATCH_NOT_FOUND, (result as Failure).value)
    }

    @Test
    fun testGetMatchByVersionSuccess() {
        val match = (matchService.enterMatch(1, MatchType.TicTacToe) as Success).value
        val result = matchService.getMatchByVersion(match.id, match.version)
        assertTrue(result is Success)
    }

    @Test
    fun testGetMatchByVersionMismatch() {
        val match = (matchService.enterMatch(1, MatchType.TicTacToe) as Success).value
        val result = matchService.getMatchByVersion(match.id, match.version + 1)
        assertTrue(result is Failure)
        assertEquals(ApiError.VERSION_MISMATCH, (result as Failure).value)
    }

    @Test
    fun testPlayMatchSuccess() {
        val match = (matchService.enterMatch(1, MatchType.TicTacToe) as Success).value
        val user1PlayerType = match.getPlayerType(1)
        val started = (matchService.enterMatch(2, MatchType.TicTacToe) as Success).value
        val turn = started.board.turn
        val move = TicTacToeMove(turn, Square(Row(0, TicTacToeBoard.BOARD_DIM), Column('a' + 0)))
        val playingUser = if (turn == user1PlayerType) started.user1 else (started.user2 as Int)
        val result = matchService.playMatch(started.id, playingUser, move, started.version)
        assertTrue(result is Success)
    }

    @Test
    fun testPlayMatchWrongUser() {
        val match = (matchService.enterMatch(1, MatchType.TicTacToe) as Success).value
        val result = matchService.playMatch(
            match.id,
            999,
            TicTacToeMove(match.board.turn, Square(Row(0, TicTacToeBoard.BOARD_DIM), Column('a' + 0))),
            match.version
        )
        assertTrue(result is Failure)
        assertEquals(ApiError.USER_NOT_IN_THIS_MATCH, (result as Failure).value)
    }

    @Test
    fun testPlayMatchWrongVersion() {
        val match = (matchService.enterMatch(1, MatchType.TicTacToe) as Success).value
        matchService.enterMatch(2, MatchType.TicTacToe)
        val result = matchService.playMatch(
            match.id,
            match.user1,
            TicTacToeMove(match.getPlayerType(match.user1), Square(Row(0, TicTacToeBoard.BOARD_DIM), Column('a' + 0))),
            match.version
        )
        assertTrue(result is Failure)
        assertEquals(ApiError.VERSION_MISMATCH, (result as Failure).value)
    }

    @Test
    fun testPlayMatchWrongPlayerType() {
        val match = (matchService.enterMatch(1, MatchType.TicTacToe) as Success).value
        val user1PlayerType = match.getPlayerType(1)
        val started = (matchService.enterMatch(2, MatchType.TicTacToe) as Success).value
        val wrongMove = TicTacToeMove(user1PlayerType.other(), Square(Row(0, TicTacToeBoard.BOARD_DIM), Column('a' + 0)))
        val result = matchService.playMatch(started.id, started.user1, wrongMove, started.version)
        assertTrue(result is Failure)
        assertEquals(ApiError.INCORRECT_PLAYER_TYPE_FOR_THIS_USER, (result as Failure).value)
    }

    @Test
    fun testForfeitSuccess() {
        val match = (matchService.enterMatch(1, MatchType.TicTacToe) as Success).value
        matchService.enterMatch(2, MatchType.TicTacToe)
        val result = matchService.forfeit(match.id, match.user1)
        assertTrue(result is Success)
        assertEquals(MatchState.WIN, (result as Success).value.state)
    }

    @Test
    fun testForfeitNotInMatch() {
        val match = (matchService.enterMatch(1, MatchType.TicTacToe) as Success).value
        val result = matchService.forfeit(match.id, 999)
        assertTrue(result is Failure)
        assertEquals(ApiError.USER_NOT_IN_THIS_MATCH, (result as Failure).value)
    }

    @Test
    fun testCancelSearchSuccess() {
        val match = (matchService.enterMatch(1, MatchType.TicTacToe) as Success).value
        val result = matchService.cancelSearch(1, match.id)
        assertTrue(result is Success)
    }

    @Test
    fun testCancelSearchWrongUser() {
        val match = (matchService.enterMatch(1, MatchType.TicTacToe) as Success).value
        val result = matchService.cancelSearch(999, match.id)
        assertTrue(result is Failure)
        assertEquals(ApiError.USER_NOT_IN_THIS_MATCH, (result as Failure).value)
    }

    @Test
    fun testCancelSearchWrongState() {
        val match = (matchService.enterMatch(1, MatchType.TicTacToe) as Success).value
        matchService.enterMatch(2, MatchType.TicTacToe)
        val result = matchService.cancelSearch(1, match.id)
        assertTrue(result is Failure)
        assertEquals(ApiError.MATCH_NOT_IN_WAITING_STATE, (result as Failure).value)
    }

    @Test
    fun testGetStatisticsEmpty() {
        val stats = matchService.getStatistics(1)
        assertTrue(stats.size == MatchType.entries.size)
        for (type in MatchType.entries) {
            assertEquals(0, stats.find{it.matchType == type.toString()}?.numberOfMatches)
        }
    }

    @Test
    fun testGetStatisticsAfterMatch() {
        val match = (matchService.enterMatch(1, MatchType.TicTacToe) as Success).value
        matchService.enterMatch(2, MatchType.TicTacToe)
        matchService.forfeit(match.id, match.user1)
        val stats = matchService.getStatistics(1)
        assertTrue(stats.isNotEmpty())
    }
}