package serviceTests

import com.crossBoard.domain.Email
import com.crossBoard.domain.UserState
import com.crossBoard.domain.Username
import com.crossBoard.repository.memoryRepositories.MemoryUserRep
import com.crossBoard.service.UsersService
import com.crossBoard.util.ApiError
import com.crossBoard.util.Failure
import com.crossBoard.util.Success
import junit.framework.TestCase.assertTrue
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UserServiceTests {
    private lateinit var userRepo: MemoryUserRep
    private lateinit var service: UsersService

    @BeforeTest
    fun setup() {
        userRepo = MemoryUserRep()
        service = UsersService(userRepo)
    }

    @Test
    fun testCreateUserSuccess() {
        val result = service.createUser(Username("john"), Email("john@test.com"), "pass123")
        assertTrue(result is Success)
        assertEquals("john", (result as Success).value.username.value)
    }

    @Test
    fun testCreateUserDuplicateUsername() {
        service.createUser(Username("john"), Email("john@test.com"), "pass123")
        val result = service.createUser(Username("john"), Email("new@test.com"), "pass123")
        assertEquals(ApiError.USERNAME_ALREADY_EXISTS, (result as Failure).value)
    }

    @Test
    fun testCreateUserDuplicateEmail() {
        service.createUser(Username("john"), Email("john@test.com"), "pass123")
        val result = service.createUser(Username("doe"), Email("john@test.com"), "pass123")
        assertEquals(ApiError.EMAIL_ALREADY_EXISTS, (result as Failure).value)
    }

    @Test
    fun testLoginSuccess() {
        service.createUser(Username("jane"), Email("jane@test.com"), "secret")
        val result = service.login(Username("jane"), "secret")
        assertTrue(result is Success)
        assertEquals("jane", (result as Success).value.username.value)
    }

    @Test
    fun testLoginUserNotFound() {
        val result = service.login(Username("ghost"), "none")
        assertEquals(ApiError.USER_NOT_FOUND, (result as Failure).value)
    }

    @Test
    fun testLoginWrongPassword() {
        service.createUser(Username("jack"), Email("jack@test.com"), "correct")
        val result = service.login(Username("jack"), "wrong")
        assertEquals(ApiError.WRONG_PASSWORD, (result as Failure).value)
    }

    @Test
    fun testUpdateUserSuccess() {
        val created = (service.createUser(Username("old"), Email("old@test.com"), "pass123") as Success).value
        val updated = service.updateUser(
            userId = created.id,
            username = Username("new"),
            email = Email("new@test.com"),
            password = "newpass",
            state = UserState.BANNED
        )
        assertTrue(updated is Success)
        assertEquals("new", (updated as Success).value.username.value)
        assertEquals("new@test.com", updated.value.email.value)
        assertEquals(UserState.BANNED.toString(), updated.value.state)
    }

    @Test
    fun testUpdateUserNotFound() {
        val result = service.updateUser(999, username = Username("ghost"))
        assertEquals(ApiError.USER_NOT_FOUND, (result as Failure).value)
    }

    @Test
    fun testGetUserByIdSuccess() {
        val created = (service.createUser(Username("ida"), Email("ida@test.com"), "pass") as Success).value
        val result = service.getUserById(created.id)
        assertTrue(result is Success)
        assertEquals("ida", (result as Success).value.username.value)
    }

    @Test
    fun testGetUserByIdNotFound() {
        val result = service.getUserById(404)
        assertEquals(ApiError.USER_NOT_FOUND, (result as Failure).value)
    }

    @Test
    fun testGetUserByTokenSuccess() {
        val user = service.createUser(Username("tokenUser"), Email("tok@test.com"), "tokpass")
        assertTrue(user is Success)
        val result = service.getUserByToken((user as Success).value.token.value)
        assertTrue(result is Success)
    }

    @Test
    fun testGetUserByTokenNotFound() {
        val result = service.getUserByToken("noToken")
        assertEquals(ApiError.USER_NOT_FOUND, (result as Failure).value)
    }

    @Test
    fun testGetUsersByNameFiltering() {
        service.createUser(Username("ann"), Email("ann@test.com"), "p1")
        service.createUser(Username("anna"), Email("anna@test.com"), "p2")
        service.createUser(Username("bobby"), Email("bob@test.com"), "p3")

        val results = service.getUsersByName("ann", 0, 10)
        assertEquals(2, results.size)
        assertTrue(results.all { it.username.value.contains("ann") })
    }
}