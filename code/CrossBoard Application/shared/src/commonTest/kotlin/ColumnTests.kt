
import crossBoard.domain.Column
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ColumnTests {
    @Test fun columnTest() {

        assertFailsWith<IllegalArgumentException> {
            Column('0')
        }

        val column = Column('a')

        assertEquals(column.index, 0)
        assertEquals(column.symbol, 'a')
    }
}