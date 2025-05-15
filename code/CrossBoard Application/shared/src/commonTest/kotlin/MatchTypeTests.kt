import com.crossBoard.domain.MatchType
import com.crossBoard.domain.toMatchType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MatchTypeTests {

    @Test fun matchTypeTest() {
        assertEquals("tic", MatchType.TicTacToe.value)
        assertEquals("tic", MatchType.TicTacToe.toString())
        assertEquals(0, MatchType.TicTacToe.ordinal)
    }

    @Test fun toMatchTypeTest() {
        assertEquals(MatchType.TicTacToe, "tic".toMatchType())

        assertFailsWith<IllegalArgumentException> {
            "invalid".toMatchType()
        }
    }
}