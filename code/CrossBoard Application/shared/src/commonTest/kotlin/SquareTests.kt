import com.crossBoard.domain.Column
import com.crossBoard.domain.Row
import com.crossBoard.domain.Square
import com.crossBoard.domain.toSquare
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SquareTests {
    @Test fun squareWithSuccess() {
        val square = Square(Row(1, 3), Column('a'))

        assertEquals(2, square.row.number)
        assertEquals(1, square.row.index)
        assertEquals('a', square.column.symbol)
        assertEquals(0, square.column.index)
    }

    @Test fun toStringWithSuccess() {
        val square = Square(Row(1, 3), Column('a'))

        assertEquals("2a", square.toString())
    }

    @Test fun toSquareWithSuccess() {
        val square = Square(Row(1, 3), Column('a'))

        val stringSquare = "2a".toSquare(3)

        //assertEquals(square, stringSquare)
        //assertEquals(square.row, stringSquare.row)
        //assertEquals(square.column, stringSquare.column)
        assertEquals(square.row.number, stringSquare.row.number)
        assertEquals(square.row.index, stringSquare.row.index)
        assertEquals(square.column.symbol, stringSquare.column.symbol)
        assertEquals(square.column.index, stringSquare.column.index)
    }

    @Test fun toSquareWithInvalidString() {
        val invalidString1 = "2"

        assertFailsWith<IllegalArgumentException> {
            invalidString1.toSquare(3)
        }

        val invalidString2 = "aA"

        assertFailsWith<IllegalArgumentException> {
            invalidString2.toSquare(3)
        }

        val invalidString3 = "2A"

        assertFailsWith<IllegalArgumentException> {
            invalidString3.toSquare(3)
        }
    }
}