package modelTests

import com.crossBoard.domain.*
import com.crossBoard.model.AuthState
import kotlin.test.*

class AuthStateTests {

    @Test fun authStateTests() {

        val result = AuthState()

        assertNull(result.user)
        assertFalse(result.isLoading)
        assertNull(result.errorMessage)
        assertTrue(result.isLoginScreenVisible)
        assertFalse(result.maintainSession)
        assertFalse(result.playMatch)
        assertEquals("", result.loginUsernameInput)
        assertEquals("", result.loginPasswordInput)
        assertEquals("", result.registerUsernameInput)
        assertEquals("", result.registerEmailInput)
        assertEquals("", result.registerPasswordInput)
        assertFalse(result.isAuthenticated)

        val user = NormalUser(1, Username("test"), Email("test@test.com"), Password("Aa12345!"), Token("test"), UserState.NORMAL)

        val result2 = AuthState(
            user = user,
            isLoading = true,
            errorMessage = "Error occurred",
            isLoginScreenVisible = false,
            maintainSession = true,
            playMatch = true,
            loginUsernameInput = "testUser",
            loginPasswordInput = "testPass",
            registerUsernameInput = "registerUser",
            registerEmailInput = "test@test.com",
            registerPasswordInput = "registerPass"
        )

        assertEquals(user, result2.user)
        assertTrue(result2.isLoading)
        assertEquals("Error occurred", result2.errorMessage)
        assertFalse(result2.isLoginScreenVisible)
        assertTrue(result2.maintainSession)
        assertTrue(result2.playMatch)
        assertEquals("testUser", result2.loginUsernameInput)
        assertEquals("testPass", result2.loginPasswordInput)
        assertEquals("registerUser", result2.registerUsernameInput)
        assertEquals("test@test.com", result2.registerEmailInput)
        assertEquals("registerPass", result2.registerPasswordInput)
        assertTrue(result2.isAuthenticated)
    }
}