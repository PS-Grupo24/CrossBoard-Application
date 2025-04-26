
import crossBoard.domain.Difficulty
import kotlin.test.Test
import kotlin.test.assertEquals

class DifficultyTests {
    @Test fun difficultyTest() {

        val easy = Difficulty.EASY

        assertEquals("EASY", easy.name)
        assertEquals(0, easy.ordinal)

        val medium = Difficulty.MEDIUM

        assertEquals("MEDIUM", medium.name)
        assertEquals(1, medium.ordinal)

        val hard = Difficulty.HARD

        assertEquals("HARD", hard.name)
        assertEquals(2, hard.ordinal)
    }
}