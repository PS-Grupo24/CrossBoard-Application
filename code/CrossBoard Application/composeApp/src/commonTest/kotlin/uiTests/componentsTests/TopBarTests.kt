package uiTests.componentsTests

import androidx.compose.ui.test.*
import com.crossBoard.domain.*
import com.crossBoard.model.MainMenuState
import com.crossBoard.model.MainScreen
import com.crossBoard.ui.components.TopBar
import com.crossBoard.ui.viewModel.MainMenuViewModel
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertTrue

class TopBarTests {

    @OptIn(ExperimentalTestApi::class)
    @Test fun topBarDisplayTests() = runComposeUiTest {

        val user = NormalUser(1, Username("test"), Email("test@test.com"), Password("Aa12345!"), Token("test"), UserState.NORMAL)
        var logoutPressed = false

        setContent {
            TopBar(
                user = user,
                mainMenuState = MainMenuState(MainScreen.Profile, topBarMessage = "test"),
                vm = MainMenuViewModel(Dispatchers.Main),
                onLogoutClick = {logoutPressed = true}
            )
        }

        onNodeWithTag("TopBar Test").assertExists()

        onNodeWithTag("TopBar Title Text Test").assertExists()
        onNodeWithTag("TopBar Title Text Test", useUnmergedTree = true).assertTextEquals("test")

        onNodeWithTag("TopBar Icon Button Test").assertExists()
        onNodeWithTag("TopBar Icon Button Icon Test", useUnmergedTree = true).assertExists()

        onNodeWithTag("TopBar Actions Icon Button Test").assertExists()
        onNodeWithTag("TopBar Actions Icon Button Icon Test", useUnmergedTree = true).assertExists()

        onNodeWithTag("TopBar Spacer Test").assertExists()

        onNodeWithTag("TopBar Logout Icon Button Test").assertExists()
        onNodeWithTag("TopBar Logout Icon Button Icon Test", useUnmergedTree = true).assertExists()
        onNodeWithTag("TopBar Logout Icon Button Test").performClick()
        assertTrue(logoutPressed)
    }
}