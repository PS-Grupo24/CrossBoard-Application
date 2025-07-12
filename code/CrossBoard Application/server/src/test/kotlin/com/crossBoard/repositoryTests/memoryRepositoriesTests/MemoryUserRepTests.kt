package com.crossBoard.repositoryTests.memoryRepositoriesTests

import com.crossBoard.domain.*
import com.crossBoard.repository.memoryRepositories.MemoryUserRep
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class MemoryUserRepTest {

    private lateinit var userRepo: MemoryUserRep

    @BeforeEach
    fun setup() {
        userRepo = MemoryUserRep()
    }

    @Test
    fun testAddUserAndGetById() {
        val username = Username("testuser")
        val email = Email("testuser@example.com")
        val password = Password("Aa12345!")

        val user = userRepo.addUser(username, email, password)
        assertNotNull(user)
        assertEquals(username, user.username)
        assertEquals(email, user.email)

        val fetchedUser = userRepo.getUserProfileById(user.id)
        assertNotNull(fetchedUser)
        assertEquals(user.id, fetchedUser?.id)
        assertEquals(username, fetchedUser?.username)
        assertEquals(email, fetchedUser?.email)
    }

    @Test
    fun testDeleteUser() {
        val user = userRepo.addUser(Username("toDelete"), Email("del@example.com"), Password("Aa12345!"))
        val deleted = userRepo.deleteUser(user.id)
        assertTrue(deleted)
        assertNull(userRepo.getUserProfileById(user.id))
    }

    @Test
    fun testUpdateUser() {
        val user = userRepo.addUser(Username("toUpdate"), Email("up@example.com"), Password("Aa12345!"))
        val newUsername = Username("updatedName")
        val newEmail = Email("updated@example.com")
        val newPassword = Password("Aa12345!")

        val updatedUser = userRepo.updateUser(user.id, newUsername, newEmail, newPassword, UserState.NORMAL)
        assertEquals(newUsername, updatedUser.username)
        assertEquals(newEmail, updatedUser.email)
    }

    @Test
    fun testGetUserByEmail() {
        val email = Email("findme@example.com")
        val user = userRepo.addUser(Username("findUser"), email, Password("Aa12345!"))
        val fetchedUser = userRepo.getUserProfileByEmail(email)
        assertNotNull(fetchedUser)
        assertEquals(user.id, fetchedUser?.id)
    }

    @Test
    fun testLoginSuccessAndFail() {
        val username = Username("loginUser")
        val password = Password("Aa12345!")
         userRepo.addUser(username, Email("login@example.com"), password)

        val success = userRepo.login(username, password)
        assertNotNull(success)

        val fail = userRepo.login(username, Password("Aa12345!!"))
        assertNull(fail)
    }

    @Test
    fun testGetUsersByNamePagination() {
        userRepo.addUser(Username("alice"), Email("alice@example.com"), Password("Aa12345!"))
        userRepo.addUser(Username("alex"), Email("alex@example.com"), Password("Aa12345!"))
        userRepo.addUser(Username("bob"), Email("bob@example.com"), Password("Aa12345!"))

        val results = userRepo.getUsersByName("al", 0, 10)
        assertTrue(results.all { it.username.value.contains("al") })

        val paginated = userRepo.getUsersByName("a", 1, 1)
        assertEquals(1, paginated.size)
    }
}
