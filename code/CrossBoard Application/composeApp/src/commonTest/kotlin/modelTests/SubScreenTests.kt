package modelTests

import com.crossBoard.model.SubScreen
import kotlin.test.Test
import kotlin.test.assertEquals

class SubScreenTests {

    @Test fun subScreenTest() {
        val result1 = SubScreen.FindMatch
        val result2 = SubScreen.Match
        val result3 = SubScreen.MatchOver

        assertEquals(SubScreen.FindMatch, result1)
        assertEquals(SubScreen.Match, result2)
        assertEquals(SubScreen.MatchOver, result3)
    }
}