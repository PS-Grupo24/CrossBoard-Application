package domainTests

import com.crossBoard.domain.Email
import com.crossBoard.domain.Password
import com.crossBoard.domain.Token
import com.crossBoard.domain.User
import com.crossBoard.domain.Username
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
/*
class UserTests {

    @Test fun usernameClassTest() {

        assertFailsWith<IllegalArgumentException> {
            Username("")
        }

        assertFailsWith<IllegalArgumentException> {
            Username("AB")
        }

        val username = Username("ABC")

        assertEquals("ABC", username.value)
    }

    @Test fun emailClassTests() {

        assertFailsWith<IllegalArgumentException> {
            Email("")
        }

        assertFailsWith<IllegalArgumentException> {
            Email("abc.com")
        }

        assertFailsWith<IllegalArgumentException> {
            Email("abc@hotmail")
        }

        val email = Email("abc@hotmail.com")

        assertEquals("abc@hotmail.com", email.value)
    }

    @Test fun passwordClassTest() {

        assertFailsWith<IllegalArgumentException> {
            Password("")
        }

        assertFailsWith<IllegalArgumentException> {
            Password("aA1!")
        }

        assertFailsWith<IllegalArgumentException> {
            Password("aaaaaa1!")
        }

        assertFailsWith<IllegalArgumentException> {
            Password("AAAAAA1!")
        }

        assertFailsWith<IllegalArgumentException> {
            Password("AaaaaaA!")
        }

        assertFailsWith<IllegalArgumentException> {
            Password("Aaaaaaa1")
        }

        val password = Password("Aa12345!")

        assertEquals("Aa12345!", password.value)
    }

    @Test fun tokenClassTest() {

        assertFailsWith<IllegalArgumentException> {
            Token("")
        }

        val token = Token("test")

        assertEquals("test", token.value)
    }

    @Test fun userClassTest() {

        assertFailsWith<IllegalArgumentException> {
            User(0, Username("ABC"), Email("abc@hotmail.com"), Password("Aa12345!"), Token("test"))
        }

        val user = User(1, Username("ABC"), Email("abc@hotmail.com"), Password("Aa12345!"), Token("test"))

        assertEquals(1, user.id)
        assertEquals("ABC", user.username.value)
        assertEquals("abc@hotmail.com", user.email.value)
        assertEquals("Aa12345!", user.password.value)
        assertEquals("test", user.token.value)
    }
}*/