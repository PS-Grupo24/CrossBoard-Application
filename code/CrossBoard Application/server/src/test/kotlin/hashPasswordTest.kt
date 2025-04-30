import junit.framework.TestCase.assertEquals
import com.crossBoard.repository.interfaces.hashPassword
import kotlin.test.Test

class hashPasswordTest {
    @Test fun `Test hash Password`() {
        val password = "Aa12345!"
        val hashPassword1 = hashPassword(password)
        val hashPassword2 = hashPassword(password)

        println("1: $hashPassword1 2: $hashPassword2")
        assertEquals(hashPassword1, hashPassword2)
    }
}