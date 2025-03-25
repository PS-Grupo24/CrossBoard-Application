
import model.Email
import model.Password
import model.User
import model.Username
import org.junit.Assert.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class UserTests {

    @Test fun usernameClassTest() {

        assertThrows(IllegalArgumentException::class.java) {
            Username("")
        }

        assertThrows(IllegalArgumentException::class.java) {
            Username("AB")
        }

        val username = Username("ABC")

        assertEquals("ABC", username.value)
    }

    @Test fun emailClassTests() {

        assertThrows(IllegalArgumentException::class.java) {
            Email("")
        }

        assertThrows(IllegalArgumentException::class.java) {
            Email("abc.com")
        }

        assertThrows(IllegalArgumentException::class.java) {
            Email("abc@hotmail")
        }

        val email = Email("abc@hotmail.com")

        assertEquals("abc@hotmail.com", email.value)
    }

    @Test fun passwordClassTest() {

        assertThrows(IllegalArgumentException::class.java) {
            Password("")
        }

        assertThrows(IllegalArgumentException::class.java) {
            Password("aA1!")
        }

        assertThrows(IllegalArgumentException::class.java) {
            Password("aaaaaa1!")
        }

        assertThrows(IllegalArgumentException::class.java) {
            Password("AAAAAA1!")
        }

        assertThrows(IllegalArgumentException::class.java) {
            Password("AaaaaaA!")
        }

        assertThrows(IllegalArgumentException::class.java) {
            Password("Aaaaaaa1")
        }

        val password = Password("Aa12345!")

        assertEquals("Aa12345!", password.value)
    }

    @Test fun userClassTest() {

        assertThrows(IllegalArgumentException::class.java) {
            User(0U, Username("ABC"), Email("abc@hotmail.com"), Password("Aa12345!"))
        }

        val user = User(1U, Username("ABC"), Email("abc@hotmail.com"), Password("Aa12345!"))

        assertEquals(1U, user.id)
        assertEquals("ABC", user.username.value)
        assertEquals("abc@hotmail.com", user.email.value)
        assertEquals("Aa12345!", user.password.value)
    }
}