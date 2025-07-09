package com.crossBoard.domainTests

import com.crossBoard.domain.Column
import kotlin.test.*

class ColumnTest {

    @Test
    fun `creates Column with valid symbol`() {
        val column = Column('b')
        assertEquals('b', column.symbol)
        assertEquals(1, column.index)
    }

    @Test
    fun `index of Column a is 0`() {
        val column = Column('a')
        assertEquals(0, column.index)
    }

    @Test
    fun `index of Column z is 25`() {
        val column = Column('z')
        assertEquals(25, column.index)
    }

    @Test
    fun `throws on uppercase symbol`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Column('A')
        }
        assertEquals("Column symbol must be between 'a' and 'z'", exception.message)
    }

    @Test
    fun `throws on non-letter symbol`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Column('1')
        }
        assertEquals("Column symbol must be between 'a' and 'z'", exception.message)
    }
}
