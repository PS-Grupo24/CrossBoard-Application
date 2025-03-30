package repository.memoryRepositories

import httpModel.UserProfileInfo
import domain.Email
import domain.Password
import domain.User
import domain.Username
import repository.interfaces.UserRepository

/**
 * Class "MemoryUserRep" represents the memory repository of the user.
 * @implements UserRepository the user repository.
 */
class MemoryUserRep : UserRepository {
    //Value storing the list of users of the app.
    private val users = mutableListOf<User>(
        User(1U, Username("Rúben Louro"), Email("A48926@alunos.isel.pt"), Password("Aa12345!")),
        User(2U, Username("Luís Reis"), Email("A48318@alunos.isel.pt"), Password("Aa12345!")),
        User(3U, Username("Pedro Pereira"), Email("palex@cc.isel.ipl.pt"), Password("Aa12345!"))
    )

    /**
     * Function "getUserProfileById" responsible to get the user information when searched by his id.
     * @param userId the id of the user being searched.
     * @return UserProfileInfo? the user profile information if found, null otherwise.
     */
    override fun getUserProfileById(userId: UInt): UserProfileInfo? {
        val u = users.find { it.id == userId } ?: return null
        return UserProfileInfo(u.id, u.username, u.email)
    }

    /**
     * Function "getUserProfileByEmail" responsible to get the user information when searched by his email.
     * @param email the email of the user being searched.
     * @return UserProfileInfo? the user profile information if found, null otherwise.
     */
    override fun getUserProfileByEmail(email: Email): UserProfileInfo? {
        val u = users.find { it.email == email } ?: return null
        return UserProfileInfo(u.id, u.username, u.email)
    }

    /**
     * Function "getUserProfileByName" responsible to get the user information when searched by his username.
     * @param username the username of the user being searched.
     * @return UserProfileInfo? the user profile information if found, null otherwise.
     */
    override fun getUserProfileByName(username: Username): UserProfileInfo? {
        val u = users.find { it.username == username } ?: return null
        return UserProfileInfo(u.id, u.username, u.email)
    }

    /**
     * Function "deleteUser" responsible to delete the user from the list of users.
     * @param userId the id of the user being deleted.
     * @return Boolean true if the user was deleted, false otherwise.
     */
    override fun deleteUser(userId: UInt): Boolean {
        return users.remove(users.find { it.id == userId })
    }

    /**
     * Function "updateUser" responsible to update the user information.
     * @param userId the id of the user being updated.
     * @param username the new username of the user.
     * @param email the new email of the user.
     * @param password the new password of the user.
     * @return UserProfileInfo the user profile information of the updated user.
     */
    override fun updateUser(
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

    /**
     * Function "getUserFullDetails" responsible to get the full details of the user.
     * @param userId the id of the user being searched.
     * @return User? the user if found, null otherwise.
     */
    override fun getUserFullDetails(userId: UInt): User? = users.find { it.id == userId }

    /**
     * Function "addUser" responsible to add a new user to the list of users.
     * @param username the username of the new user.
     * @param email the email of the new user.
     * @param password the password of the new user.
     * @return UserProfileInfo the user profile information of the new user.
     */
    override fun addUser(
        username: Username,
        email: Email,
        password: Password
    ): UserProfileInfo {
        val lastId = if(users.isEmpty()) 0U else users.maxOf { it.id }

        val newUser = User(lastId + 1U, username, email, password)
        users.add(newUser)
        return UserProfileInfo(newUser.id, newUser.username, newUser.email)
    }
}