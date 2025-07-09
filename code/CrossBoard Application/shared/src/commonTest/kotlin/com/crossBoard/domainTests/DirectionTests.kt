package com.crossBoard.domainTests

import kotlin.test.*
import com.crossBoard.domain.*

class DirectionTests {

    @Test
    fun `Direction enum has correct difRow and difCol`() {
        val expected = mapOf(
            Direction.UP to Pair(-1, 0),
            Direction.DOWN to Pair(1, 0),
            Direction.LEFT to Pair(0, -1),
            Direction.RIGHT to Pair(0, 1),
            Direction.UP_LEFT to Pair(-1, -1),
            Direction.UP_RIGHT to Pair(-1, 1),
            Direction.DOWN_LEFT to Pair(1, -1),
            Direction.DOWN_RIGHT to Pair(1, 1)
        )

        for (direction in Direction.entries) {
            val (expectedRow, expectedCol) = expected[direction]
                ?: error("Missing direction: $direction")
            assertEquals(expectedRow, direction.difRow, "Row mismatch for $direction")
            assertEquals(expectedCol, direction.difCol, "Col mismatch for $direction")
        }
    }

    @Test
    fun `Direction entries contain all 8 directions`() {
        assertEquals(8, Direction.entries.size)
        val expectedNames = setOf(
            "UP", "DOWN", "LEFT", "RIGHT",
            "UP_LEFT", "UP_RIGHT", "DOWN_LEFT", "DOWN_RIGHT"
        )
        assertEquals(expectedNames, Direction.entries.map { it.name }.toSet())
    }
}
