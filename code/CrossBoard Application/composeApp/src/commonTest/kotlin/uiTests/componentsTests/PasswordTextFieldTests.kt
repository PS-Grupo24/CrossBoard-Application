package uiTests.componentsTests

import androidx.compose.material.TextFieldDefaults
import androidx.compose.ui.test.*
import com.crossBoard.ui.components.PasswordTextField
import kotlin.test.Test
import kotlin.test.assertEquals
/*
class PasswordTextFieldTests {

    @OptIn(ExperimentalTestApi::class)
    @Test fun passwordTextFieldDisplayTests() = runComposeUiTest {

        var currentText = ""
        setContent {
            PasswordTextField(
                value = currentText,
                errorMessage = null,
                onValueChange = { currentText = it },
                textFieldColors = TextFieldDefaults.outlinedTextFieldColors()
            )
        }

        onNodeWithTag("PasswordTextField Test").assertExists()

        onNodeWithTag("PasswordTextField Label Test", useUnmergedTree = true).assertExists().assertTextEquals("Password")

        onNodeWithTag("PasswordTextField Test").performTextInput("testPassword")
        assertEquals("testPassword", currentText)

        onNodeWithTag("PasswordTextField Icon Test").assertExists()

        onNodeWithTag("PasswordTextField Icon Image Test", useUnmergedTree = true).assertExists()
        onNodeWithTag("PasswordTextField Icon Test").performClick()

        onNodeWithTag("PasswordTextField Label Test", useUnmergedTree = true).isDisplayed()
    }
}*/