package com.crossBoard.domainTests

import com.crossBoard.domain.Row
import kotlin.test.*

class RowTest {

    @Test
    fun `creates Row with valid index and boardDim`() {
        val row = Row(2, 8)
        assertEquals(2, row.index)
        assertEquals(8, row.boardDim)
        assertEquals(6, row.number) // 8 - 2 = 6
    }

    @Test
    fun `row number is boardDim minus index`() {
        val row = Row(0, 5)
        assertEquals(5, row.number)

        val row2 = Row(4, 5)
        assertEquals(1, row2.number)
    }

    @Test
    fun `throws for index less than 0`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Row(-1, 5)
        }
        assertEquals("Index must be between 0 and Board Dimension", exception.message)
    }

    @Test
    fun `throws for index equal to boardDim`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Row(5, 5)
        }
        assertEquals("Index must be between 0 and Board Dimension", exception.message)
    }

    @Test
    fun `throws for index greater than boardDim`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Row(6, 5)
        }
        assertEquals("Index must be between 0 and Board Dimension", exception.message)
    }
}
