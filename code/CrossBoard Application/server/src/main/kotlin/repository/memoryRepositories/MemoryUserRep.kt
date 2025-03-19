package repository.memoryRepositories

import httpModel.UserProfileInfo
import model.Email
import model.Password
import model.User
import model.Username
import repository.interfaces.UserRepository
import kotlin.random.Random
import kotlin.random.nextUInt

object MemoryUserRep : UserRepository {
    private val users = mutableListOf<User>(
        User(1U, Username("Rúben Louro"), Email("A48926@alunos.isel.pt"), Password("Aa12345!")),
        User(2U, Username("Luís Reis"), Email("A48318@alunos.isel.pt"), Password("Aa12345!")),
        User(3U, Username("Pedro Pereira"), Email("palex@cc.isel.ipl.pt"), Password("Aa12345"))
    )
    override suspend fun getUserProfileById(userId: UInt): UserProfileInfo? {
        val u = users.find { it.id == userId } ?: return null
        return UserProfileInfo(u.id, u.username, u.email)
    }

    override suspend fun getUserProfileByEmail(email: Email): UserProfileInfo? {
        val u = users.find { it.email == email } ?: return null
        return UserProfileInfo(u.id, u.username, u.email)
    }

    override suspend fun getUserProfileByName(username: Username): UserProfileInfo? {
        val u = users.find { it.username == username } ?: return null
        return UserProfileInfo(u.id, u.username, u.email)
    }

    override suspend fun deleteUser(userId: UInt): Boolean {
        return users.remove(users.find { it.id == userId })
    }

    override suspend fun updateUser(
        userId: UInt,
        username: Username?,
        email: Email?,
        password: Password?
    ): UserProfileInfo {
        val user = getUserFullDetails(userId)!!
        val newName = username ?: user.username
        val newEmail = email ?: user.email
        val newPassword = password ?: user.password

        val updatedUser = User(user.id, newName, newEmail, newPassword)
        users.remove(user)
        users.add(updatedUser)
        return UserProfileInfo(updatedUser.id, updatedUser.username, updatedUser.email)
    }

    override suspend fun getUserFullDetails(userId: UInt): User? {
        return users.find { it.id == userId }
    }
    override suspend fun addUser(
        username: Username,
        email: Email,
        password: Password
    ): UserProfileInfo {
        val newUser = User(Random.nextUInt(), username, email, password)
        users.add(newUser)
        return UserProfileInfo(newUser.id, newUser.username, newUser.email)
    }
}