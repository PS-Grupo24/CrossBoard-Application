package com.crossBoard.repositoryTests.jdbcTests

import com.crossBoard.domain.*
import com.crossBoard.domain.matchModule.ReversiModule
import com.crossBoard.domain.matchModule.TicTacToeModule
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.repository.interfaces.MatchRepository
import com.crossBoard.repository.interfaces.UserRepository
import com.crossBoard.repository.jdbc.JdbcMatchRepo
import com.crossBoard.repository.jdbc.JdbcUserRepo
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.postgresql.ds.PGSimpleDataSource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import javax.sql.DataSource

@Testcontainers
class JdbcMatchRepoTests {
    private lateinit var matchRepo: MatchRepository
    private lateinit var userRepo: UserRepository

    companion object {
        @Container
        @JvmStatic
        private val postgresContainer = PostgreSQLContainer("postgres:15-alpine")

        private lateinit var dataSource: DataSource

        @BeforeAll
        @JvmStatic
        fun `setup database and schema`() {
            val ds = PGSimpleDataSource().apply {
                setUrl(postgresContainer.jdbcUrl)
                user = postgresContainer.username
                password = postgresContainer.password
            }
            dataSource = ds

            val schemaSql = javaClass.getResource("/createTable.sql")!!.readText()
            dataSource.connection.use { it.createStatement().execute(schemaSql) }
        }
    }

    @BeforeEach
    fun `setup repositories and clear data`() {
        userRepo = JdbcUserRepo(dataSource)
        matchRepo = JdbcMatchRepo(dataSource)

        dataSource.connection.use { conn ->
            conn.createStatement().execute("DELETE FROM match")
            conn.createStatement().execute("DELETE FROM users")
        }
    }

    @Test
    fun `addMatch should insert a match and getMatchById should retrieve it`() {
        val user1 = userRepo.addUser(Username("player1"), Email("p1@test.com"), "pass")
        val match = MultiPlayerMatch(TicTacToeModule().getInitialBoard(), 101, MatchState.WAITING, user1.id, null, MatchType.TicTacToe, 1, null)

        matchRepo.addMatch(match)
        val foundMatch = matchRepo.getMatchById(101)

        assertNotNull(foundMatch)
        assertEquals(101, foundMatch.id)
        assertEquals(user1.id, foundMatch.user1)
        assertNull(foundMatch.user2)
    }

    @Test
    fun `getRunningMatchByUser should find a WAITING match`() {
        val user1 = userRepo.addUser(Username("player1"), Email("p1@test.com"), "pass")
        val waitingMatch = MultiPlayerMatch(ReversiModule().getInitialBoard(), 102, MatchState.WAITING, user1.id, null, MatchType.Reversi, 1, null)
        matchRepo.addMatch(waitingMatch)

        val foundMatch = matchRepo.getRunningMatchByUser(user1.id)

        assertNotNull(foundMatch)
        assertEquals(102, foundMatch.id)
    }

    @Test
    fun `getRunningMatchByUser should return null if match is finished`() {

        val user1 = userRepo.addUser(Username("player1"), Email("p1@test.com"), "pass")
        val user2 = userRepo.addUser(Username("player2"), Email("p2@test.com"), "pass")
        val runningMatch = MultiPlayerMatch(TicTacToeModule().getInitialBoard(), 103, MatchState.RUNNING, user1.id, user2.id, MatchType.TicTacToe, 1, null)
        matchRepo.addMatch(runningMatch)
        matchRepo.updateMatch(103, runningMatch.board.forfeit(runningMatch.getPlayerType(user1.id)), user1.id, user2.id, MatchType.TicTacToe, 2, MatchState.WIN, user1.id)


        val foundMatch = matchRepo.getRunningMatchByUser(user1.id)

        assertNull(foundMatch, "Should not find a match that has already finished.")
    }

    @Test
    fun `updateMatch should correctly change state and add player2`() {
        val module = TicTacToeModule()
        val user1 = userRepo.addUser(Username("player1"), Email("p1@test.com"), "pass")
        val user2 = userRepo.addUser(Username("player2"), Email("p2@test.com"), "pass")
        val initialMatch = MultiPlayerMatch(module.getInitialBoard(), 104, MatchState.WAITING, user1.id, null, MatchType.TicTacToe, 1, null)
        matchRepo.addMatch(initialMatch)
        val played = initialMatch.play(TicTacToeMove(initialMatch.board.turn, module.getSquare(1, 1)))

        val finalBoard = played.board
        val updatedMatch = matchRepo.updateMatch(104, finalBoard, user1.id, user2.id, MatchType.TicTacToe, 2, MatchState.RUNNING, null)

        assertEquals(2, updatedMatch.version)
        assertEquals(MatchState.RUNNING, updatedMatch.state)
        assertEquals(user2.id, updatedMatch.user2)

        val fetchedAfterUpdate = matchRepo.getMatchById(104)
        assertNotNull(fetchedAfterUpdate)
        assertEquals(MatchState.RUNNING, fetchedAfterUpdate.state)
        assertEquals(user2.id, fetchedAfterUpdate.user2)
    }

    @Test
    fun `getStatistics should calculate stats correctly across different game types`() {
        val user1 = userRepo.addUser(Username("player1"), Email("p1@test.com"), "pass")
        val user2 = userRepo.addUser(Username("player2"), Email("p2@test.com"), "pass")

        val initialTicBoard = TicTacToeModule().getInitialBoard()
        val initialRevBoard = ReversiModule().getInitialBoard()

        val firstMId = matchRepo.addMatch(
            MultiPlayerMatch(
                board = initialTicBoard,
                id = 301,
                state = MatchState.RUNNING,
                user1 = user1.id,
                user2 = user2.id,
                MatchType.TicTacToe,
                version = 1,
                winner = null)
        )
        val firstM = matchRepo.getMatchById(firstMId)
        assertNotNull(firstM)
        println(firstM.user1)
        println(firstM.user2)
        matchRepo.updateMatch(301, firstM.forfeit(user2.id).board, user1.id, user2.id, MatchType.TicTacToe, 2, MatchState.WIN, user1.id)

        val revMatch = MultiPlayerMatch(initialRevBoard,302,MatchState.RUNNING,user1.id,user2.id,MatchType.Reversi,1,null)
        matchRepo.addMatch(revMatch)
        matchRepo.updateMatch(302, revMatch.forfeit(user1.id).board, user1.id, user2.id, MatchType.Reversi, 2, MatchState.WIN, user2.id)

        matchRepo.addMatch(MultiPlayerMatch(initialTicBoard,303,MatchState.RUNNING,user1.id,user2.id,MatchType.TicTacToe,1,null))
        matchRepo.updateMatch(303, initialTicBoard, user1.id, user2.id, MatchType.TicTacToe, 2, MatchState.DRAW, null)

        val stats = matchRepo.getStatistics(user1.id)

        val tttStats = stats.find { it.matchType == MatchType.TicTacToe.name }
        assertNotNull(tttStats)
        assertEquals(2, tttStats.numberOfMatches, "TicTacToe should have 2 completed matches")
        assertEquals(1, tttStats.numberOfWins)
        assertEquals(1, tttStats.numberOfDraws)
        assertEquals(0, tttStats.numberOfLosses)
        assertEquals(0.5, tttStats.averageWinningRate, 0.001)

        val reversiStats = stats.find { it.matchType == MatchType.Reversi.name }
        assertNotNull(reversiStats)
        assertEquals(1, reversiStats.numberOfMatches)
        assertEquals(0, reversiStats.numberOfWins)
        assertEquals(1, reversiStats.numberOfLosses)
        assertEquals(0, reversiStats.numberOfDraws)
    }
}