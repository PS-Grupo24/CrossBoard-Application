package modelTests

import com.crossBoard.model.MainMenuState
import com.crossBoard.model.MainScreen
import com.crossBoard.model.SubScreen
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MainMenuStateTests {

    @Test fun mainMenuStateTest() {

        val result = MainMenuState(topBarMessage = "testBar")

        assertEquals(MainScreen.MainMenu, result.currentMainScreen)
        assertNull(result.currentSubScreen)
        assertEquals("testBar", result.topBarMessage)

        val result2 = MainMenuState(
            currentMainScreen = MainScreen.Profile,
            currentSubScreen = SubScreen.Match,
            topBarMessage = "testBar"
        )

        assertEquals(MainScreen.Profile, result2.currentMainScreen)
        assertEquals(SubScreen.Match, result2.currentSubScreen)
        assertEquals("testBar", result2.topBarMessage)
    }
}