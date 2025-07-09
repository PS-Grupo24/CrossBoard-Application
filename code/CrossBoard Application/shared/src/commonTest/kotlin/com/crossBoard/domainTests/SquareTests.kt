package com.crossBoard.domainTests

import com.crossBoard.domain.Column
import com.crossBoard.domain.Direction
import com.crossBoard.domain.Row
import com.crossBoard.domain.Square
import com.crossBoard.domain.toSquare
import kotlin.test.*

class SquareTest {

    private val boardDim = 8

    @Test
    fun `toString returns correct notation`() {
        val square = Square(Row(2, boardDim), Column('c'))
        assertEquals("6c", square.toString())
    }

    @Test
    fun `adjust returns new Square in correct direction`() {
        val square = Square(Row(3, boardDim), Column('d')) // 5d
        val direction = Direction.UP_RIGHT
        val adjusted = square.adjust(direction)
        assertNotNull(adjusted)
        assertEquals("6e", adjusted.toString())
    }

    @Test
    fun `adjust returns null if out of bounds`() {
        val square = Square(Row(0, boardDim), Column('a'))
        val direction = Direction.UP_LEFT
        val adjusted = square.adjust(direction)
        assertNull(adjusted)
    }

    @Test
    fun `toSquare converts string to correct Square`() {
        val square = "3c".toSquare(boardDim)
        assertEquals(5, square.row.index) // 8 - 3 = 5
        assertEquals('c', square.column.symbol)
    }

    @Test
    fun `toSquare throws on invalid length`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            "c".toSquare(boardDim)
        }
        assertEquals("The square input must have 2 characters.", exception.message)
    }

    @Test
    fun `toSquare throws if first char is not digit`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            "cc".toSquare(boardDim)
        }
        assertEquals("First Char is not a digit", exception.message)
    }

    @Test
    fun `toSquare throws if second char is not lowercase letter`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            "3C".toSquare(boardDim)
        }
        assertEquals("Second Char isn't a lower case letter", exception.message)
    }
}
