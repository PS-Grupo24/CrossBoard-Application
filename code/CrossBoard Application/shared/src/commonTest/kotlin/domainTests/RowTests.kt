package domainTests

import com.crossBoard.domain.Row
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RowTests {
    @Test fun rowTest() {
        assertFailsWith<IllegalArgumentException> {
            Row(1, 0)
        }

        val row = Row(1, 3)
        val row2 = Row(1, 3)
        assertEquals(row, row2)
    }
}
