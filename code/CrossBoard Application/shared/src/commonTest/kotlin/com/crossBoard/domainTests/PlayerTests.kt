package com.crossBoard.domainTests

import com.crossBoard.domain.Player
import com.crossBoard.domain.toPlayer
import kotlin.test.*

class PlayerTests {

    @Test
    fun `Player enum values have correct name and ordinal`() {
        assertEquals("WHITE", Player.WHITE.name)
        assertEquals(0, Player.WHITE.ordinal)

        assertEquals("BLACK", Player.BLACK.name)
        assertEquals(1, Player.BLACK.ordinal)

        assertEquals("EMPTY", Player.EMPTY.name)
        assertEquals(2, Player.EMPTY.ordinal)
    }

    @Test
    fun `Player other() returns correct counterpart`() {
        assertEquals(Player.BLACK, Player.WHITE.other())
        assertEquals(Player.WHITE, Player.BLACK.other())
        assertEquals(Player.EMPTY, Player.EMPTY.other())
    }

    @Test
    fun `Player toString() returns name`() {
        assertEquals("WHITE", Player.WHITE.toString())
        assertEquals("BLACK", Player.BLACK.toString())
        assertEquals("EMPTY", Player.EMPTY.toString())
    }

    @Test
    fun `String toPlayer() maps correctly to Player enum`() {
        assertEquals(Player.BLACK, "BLACK".toPlayer())
        assertEquals(Player.WHITE, "WHITE".toPlayer())
        assertEquals(Player.EMPTY, "EMPTY".toPlayer())
    }

    @Test
    fun `String toPlayer() throws on invalid value`() {
        assertFailsWith<IllegalArgumentException> {
            "INVALID".toPlayer()
        }
    }
}
