package com.crossBoard.domainTests

import kotlin.test.*
import com.crossBoard.domain.*

class UserTests {

    // --- Username ---
    @Test
    fun `Username accepts valid value`() {
        val username = Username("User123")
        assertEquals("User123", username.value)
    }

    @Test
    fun `Username throws on blank or too short`() {
        assertFailsWith<IllegalArgumentException> { Username("") }
        assertFailsWith<IllegalArgumentException> { Username("  ") }
        assertFailsWith<IllegalArgumentException> { Username("ab") }
    }

    // --- Email ---
    @Test
    fun `Email accepts valid com and pt emails`() {
        val email1 = Email("test@example.com")
        val email2 = Email("user@domain.pt")
        assertEquals("test@example.com", email1.value)
        assertEquals("user@domain.pt", email2.value)
    }

    @Test
    fun `Email throws on invalid formats`() {
        assertFailsWith<IllegalArgumentException> { Email("") }
        assertFailsWith<IllegalArgumentException> { Email("no-at-symbol.com") }
        assertFailsWith<IllegalArgumentException> { Email("missing@dot") }
        assertFailsWith<IllegalArgumentException> { Email("no-ending@domain.org") }
    }

    // --- Password ---
    @Test
    fun `Password accepts strong valid password`() {
        val password = Password("Strong1!")
        assertEquals("Strong1!", password.value)
    }

    @Test
    fun `Password throws on weak values`() {
        assertFailsWith<IllegalArgumentException> { Password("") } // blank
        assertFailsWith<IllegalArgumentException> { Password("short1!") } // too short
        assertFailsWith<IllegalArgumentException> { Password("alllowercase1!") } // no uppercase
        assertFailsWith<IllegalArgumentException> { Password("ALLUPPERCASE1!") } // no lowercase
        assertFailsWith<IllegalArgumentException> { Password("NoNumber!") } // no number
        assertFailsWith<IllegalArgumentException> { Password("NoSpecial1") } // no special char
    }

    // --- Token ---
    @Test
    fun `Token accepts valid value`() {
        val token = Token("abc123")
        assertEquals("abc123", token.value)
    }

    @Test
    fun `Token throws on blank`() {
        assertFailsWith<IllegalArgumentException> { Token("") }
        assertFailsWith<IllegalArgumentException> { Token(" ") }
    }

    // --- NormalUser ---
    @Test
    fun `NormalUser accepts valid data`() {
        val user = NormalUser(
            id = 1,
            username = Username("User123"),
            email = Email("user@example.com"),
            password = Password("Pass123!"),
            token = Token("token123"),
            state = UserState.NORMAL
        )

        assertEquals(1, user.id)
        assertEquals(UserState.NORMAL, user.state)
    }

    @Test
    fun `NormalUser throws on invalid id`() {
        assertFailsWith<IllegalArgumentException> {
            NormalUser(
                id = 0,
                username = Username("User123"),
                email = Email("user@example.com"),
                password = Password("Pass123!"),
                token = Token("token123"),
                state = UserState.NORMAL
            )
        }
    }

    // --- Admin ---
    @Test
    fun `Admin accepts valid data`() {
        val admin = Admin(
            id = 5,
            username = Username("Admin01"),
            email = Email("admin@example.com"),
            password = Password("Admin1234124!"),
            token = Token("adminToken")
        )

        assertEquals(5, admin.id)
        assertEquals("Admin", Admin.STATE)
    }

    // --- UserInfo ---
    @Test
    fun `UserInfo holds correct user summary`() {
        val info = UserInfo(
            id = 42,
            token = Token("tokenABC"),
            username = Username("Shorty"),
            email = Email("shorty@web.pt"),
            state = "NORMAL"
        )

        assertEquals("Shorty", info.username.value)
        assertEquals("shorty@web.pt", info.email.value)
        assertEquals("NORMAL", info.state)
    }
}
