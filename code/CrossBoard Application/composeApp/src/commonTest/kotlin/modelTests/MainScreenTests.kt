package modelTests

import com.crossBoard.model.MainScreen
import kotlin.test.Test
import kotlin.test.assertEquals

class MainScreenTests {

    @Test fun mainScreenTest() {

        val result1 = MainScreen.MainMenu
        val result2 = MainScreen.GameFlow
        val result3 = MainScreen.Profile
        val result4 = MainScreen.Statistics
        val result5 = MainScreen.SinglePlayer
        val result6 = MainScreen.AdminPanel

        assertEquals(MainScreen.MainMenu, result1)
        assertEquals(MainScreen.GameFlow, result2)
        assertEquals(MainScreen.Profile, result3)
        assertEquals(MainScreen.Statistics, result4)
        assertEquals(MainScreen.SinglePlayer, result5)
        assertEquals(MainScreen.AdminPanel, result6)
    }
}