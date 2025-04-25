
import domain.Row
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RowTests {

    @Test fun rowTest() {

        assertFailsWith<IllegalArgumentException> {
            Row(1, 0)
        }

        val row = Row(1, 3)

        assertEquals(2, row.number)
        assertEquals(1, row.index)
    }
}