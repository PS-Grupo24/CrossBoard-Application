package com.crossBoard.domainTests

import kotlin.test.*
import com.crossBoard.domain.*

class MatchStateTests {

    @Test
    fun `MatchState toString returns correct name`() {
        assertEquals("WAITING", MatchState.WAITING.toString())
        assertEquals("RUNNING", MatchState.RUNNING.toString())
        assertEquals("DRAW", MatchState.DRAW.toString())
        assertEquals("WIN", MatchState.WIN.toString())
    }

    @Test
    fun `String toMatchState converts valid strings`() {
        assertEquals(MatchState.WAITING, "WAITING".toMatchState())
        assertEquals(MatchState.RUNNING, "RUNNING".toMatchState())
        assertEquals(MatchState.DRAW, "DRAW".toMatchState())
        assertEquals(MatchState.WIN, "WIN".toMatchState())
    }

    @Test
    fun `String toMatchState throws on invalid input`() {
        assertFailsWith<IllegalArgumentException> {
            "INVALID".toMatchState()
        }
    }
}
