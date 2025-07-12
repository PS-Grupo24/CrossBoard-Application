package uiTests.componentsTests

import androidx.compose.ui.test.*
import com.crossBoard.ui.components.MyAlertDialog
import kotlin.test.Test
import kotlin.test.assertTrue


class AlertDialogTests {

    @OptIn(ExperimentalTestApi::class)
    @Test fun alertDialogDisplayTests() = runComposeUiTest {
        var confirmClicked = false
        var dismissClicked = false
        var dismissedViaRequest = false

        setContent {
            MyAlertDialog(
                onDismissRequest = { dismissedViaRequest = true },
                title = "Test Alert",
                text = "This is a test alert dialog.",
                onConfirm = { confirmClicked = true },
                confirmText = "Confirm",
                onDismiss = { dismissClicked = true },
                dismissText = "Dismiss"
            )
        }

        onNodeWithTag("Alert Dialog Title test").assertExists()
        onNodeWithTag("Alert Dialog Text test").isDisplayed()

        onNodeWithTag("Confirm Button Test").assertExists()
        onNodeWithTag("Confirm Button Text Test", useUnmergedTree = true).isDisplayed()

        onNodeWithTag("Dismiss Button Test").assertExists()
        onNodeWithTag("Dismiss Button Text Test", useUnmergedTree = true).isDisplayed()

        onNodeWithTag("Confirm Button Test").performClick()
        assertTrue(confirmClicked)

        onNodeWithTag("Dismiss Button Test").performClick()
        assertTrue(dismissClicked)

        runOnIdle {
            dismissedViaRequest = true
        }

        assertTrue(dismissedViaRequest)
    }

}