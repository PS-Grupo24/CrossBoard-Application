package com.crossBoard.domainTests

import kotlin.test.*
import com.crossBoard.domain.*

class MatchTypeTests {

    @Test
    fun `MatchType toString returns enum name`() {
        for (type in MatchType.entries) {
            assertEquals(type.name, type.toString())
        }
    }

    @Test
    fun `String toMatchType returns correct enum`() {
        for (type in MatchType.entries) {
            assertEquals(type, type.name.toMatchType())
        }
    }

    @Test
    fun `String toMatchType throws on invalid input`() {
        assertFailsWith<IllegalArgumentException> {
            "InvalidGame".toMatchType()
        }
    }
}
