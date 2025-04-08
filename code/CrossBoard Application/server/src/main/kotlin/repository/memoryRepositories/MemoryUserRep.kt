package repository.memoryRepositories

import domain.*
import httpModel.UserProfileOutput
import repository.interfaces.UserRepository

/**
 * Class "MemoryUserRep" represents the memory repository of the user.
 * @implements UserRepository the user repository.
 */
class MemoryUserRep : UserRepository {
    //Value storing the list of users of the app.
    private val users = mutableListOf<User>(
        User(1, Username("Rúben Louro"), Email("A48926@alunos.isel.pt"), Password("Aa12345!"), "1"),
        User(2, Username("Luís Reis"), Email("A48318@alunos.isel.pt"), Password("Aa12345!"), "2"),
        User(3, Username("Pedro Pereira"), Email("palex@cc.isel.ipl.pt"), Password("Aa12345!"), "3"),
    )

    /**
     * Function "getUserProfileById" responsible to get the user information when searched by his id.
     * @param userId the id of the user being searched.
     * @return UserProfileInfo? the user profile information if found, null otherwise.
     */
    override fun getUserProfileById(userId: Int): UserProfileOutput? {
        val u = users.find { it.id == userId } ?: return null
        return UserProfileOutput(u.id, u.username.value, u.email.value, u.token)
    }

    /**
     * Function "getUserProfileByEmail" responsible to get the user information when searched by his email.
     * @param email the email of the user being searched.
     * @return UserProfileInfo? the user profile information if found, null otherwise.
     */
    override fun getUserProfileByEmail(email: Email): UserProfileOutput? {
        val u = users.find { it.email == email } ?: return null
        return UserProfileOutput(u.id, u.username.value, u.email.value, u.token)
    }

    /**
     * Function "getUserProfileByName" responsible to get the user information when searched by his username.
     * @param username the username of the user being searched.
     * @return UserProfileInfo? the user profile information if found, null otherwise.
     */
    override fun getUserProfileByName(username: Username): UserProfileOutput? {
        val u = users.find { it.username == username } ?: return null
        return UserProfileOutput(u.id, u.username.value, u.email.value, u.token)
    }

    /**
     * Function "deleteUser" responsible to delete the user from the list of users.
     * @param userId the id of the user being deleted.
     * @return Boolean true if the user was deleted, false otherwise.
     */
    override fun deleteUser(userId: Int): Boolean {
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
        userId: Int,
        username: Username?,
        email: Email?,
        password: Password?
    ): UserProfileOutput {
        val user = getUserFullDetails(userId)!!
        val newName = username ?: user.username
        val newEmail = email ?: user.email
        val newPassword = password ?: user.password

        val updatedUser = User(user.id, newName, newEmail, newPassword, user.token)
        users.remove(user)
        users.add(updatedUser)
        return UserProfileOutput(updatedUser.id, updatedUser.username.value, updatedUser.email.value, updatedUser.token)
    }

    /**
     * Function "getUserFullDetails" responsible to get the full details of the user.
     * @param userId the id of the user being searched.
     * @return User? the user if found, null otherwise.
     */
    override fun getUserFullDetails(userId: Int): User? = users.find { it.id == userId }

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
    ): UserProfileOutput {
        val lastId = if(users.isEmpty()) 0 else users.maxOf { it.id }

        val newUser = User(lastId + 1, username, email, password, generateTokenValue())
        users.add(newUser)
        return UserProfileOutput(newUser.id, newUser.username.value, newUser.email.value, newUser.token)
    }

    override fun getUserProfileByToken(token: String): UserProfileOutput? {
        val u = users.find { it.token == token } ?: return null
        return UserProfileOutput(u.id, u.username.value, u.email.value, u.token)
    }
}