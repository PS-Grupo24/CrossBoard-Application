package com.crossBoard.httpModelTests

import com.crossBoard.httpModel.MatchStats
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MatchStatsTest {

    private val json = Json { encodeDefaults = true }

    @Test
    fun `create MatchStats with valid values`() {
        val stats = MatchStats(
            matchType = "Reversi",
            numberOfMatches = 20,
            numberOfWins = 10,
            numberOfDraws = 5,
            numberOfLosses = 5,
            averageWinningRate = 0.5
        )

        assertEquals("Reversi", stats.matchType)
        assertEquals(20, stats.numberOfMatches)
        assertEquals(10, stats.numberOfWins)
        assertEquals(5, stats.numberOfDraws)
        assertEquals(5, stats.numberOfLosses)
        assertEquals(0.5, stats.averageWinningRate)
    }

    @Test
    fun `serialize MatchStats to JSON`() {
        val stats = MatchStats("TicTacToe", 30, 15, 10, 5, 0.5)
        val jsonString = json.encodeToString(MatchStats.serializer(), stats)

        assertEquals(
            """{"matchType":"TicTacToe","numberOfMatches":30,"numberOfWins":15,"numberOfDraws":10,"numberOfLosses":5,"averageWinningRate":0.5}""",
            jsonString
        )
    }

    @Test
    fun `deserialize JSON to MatchStats`() {
        val jsonString = """{
            "matchType": "TicTacToe",
            "numberOfMatches": 50,
            "numberOfWins": 25,
            "numberOfDraws": 10,
            "numberOfLosses": 15,
            "averageWinningRate": 0.5
        }"""
        val stats = json.decodeFromString(MatchStats.serializer(), jsonString)

        assertNotNull(stats)
        assertEquals("TicTacToe", stats.matchType)
        assertEquals(50, stats.numberOfMatches)
        assertEquals(25, stats.numberOfWins)
        assertEquals(10, stats.numberOfDraws)
        assertEquals(15, stats.numberOfLosses)
        assertEquals(0.5, stats.averageWinningRate)
    }
}
