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
        val passwordHash = "hashedPassword"

        val user = userRepo.addUser(username, email, passwordHash)
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
        val user = userRepo.addUser(Username("toDelete"), Email("del@example.com"), "pw")
        val deleted = userRepo.deleteUser(user.id)
        assertTrue(deleted)
        assertNull(userRepo.getUserProfileById(user.id))
    }

    @Test
    fun testUpdateUser() {
        val user = userRepo.addUser(Username("toUpdate"), Email("up@example.com"), "pw")
        val newUsername = Username("updatedName")
        val newEmail = Email("updated@example.com")
        val newPasswordHash = "newHash"

        val updatedUser = userRepo.updateUser(user.id, newUsername, newEmail, newPasswordHash, UserState.NORMAL)
        assertEquals(newUsername, updatedUser.username)
        assertEquals(newEmail, updatedUser.email)
    }

    @Test
    fun testGetUserByEmail() {
        val email = Email("findme@example.com")
        val user = userRepo.addUser(Username("findUser"), email, "pw")
        val fetchedUser = userRepo.getUserProfileByEmail(email)
        assertNotNull(fetchedUser)
        assertEquals(user.id, fetchedUser?.id)
    }

    @Test
    fun testLoginSuccessAndFail() {
        val username = Username("loginUser")
        val hashedPassword = "hashedPass"
         userRepo.addUser(username, Email("login@example.com"), hashedPassword)

        val success = userRepo.login(username, hashedPassword)
        assertNotNull(success)

        val fail = userRepo.login(username, "wrongPassword")
        assertNull(fail)
    }

    @Test
    fun testGetUsersByNamePagination() {
        userRepo.addUser(Username("alice"), Email("alice@example.com"), "pw")
        userRepo.addUser(Username("alex"), Email("alex@example.com"), "pw")
        userRepo.addUser(Username("bob"), Email("bob@example.com"), "pw")

        val results = userRepo.getUsersByName("al", 0, 10)
        assertTrue(results.all { it.username.value.contains("al") })

        val paginated = userRepo.getUsersByName("a", 1, 1)
        assertEquals(1, paginated.size)
    }
}
