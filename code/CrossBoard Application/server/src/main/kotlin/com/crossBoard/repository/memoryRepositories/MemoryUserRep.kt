package com.crossBoard.repository.memoryRepositories

import com.crossBoard.domain.Admin
import com.crossBoard.domain.Email
import com.crossBoard.domain.NormalUser
import com.crossBoard.domain.Password
import com.crossBoard.domain.Token
import com.crossBoard.domain.User
import com.crossBoard.domain.UserInfo
import com.crossBoard.domain.UserState
import com.crossBoard.domain.Username
import com.crossBoard.repository.interfaces.UserRepository
import com.crossBoard.repository.interfaces.generateTokenValue
import com.crossBoard.repository.interfaces.hashPassword

/**
 * Class "MemoryUserRep" represents the memory repository of the user.
 * @implements UserRepository the user repository.
 */
class MemoryUserRep : UserRepository {

    /**
     * List of users stored in memory.
     */
    private val users = mutableListOf<User>(
        Admin(1, Username("Rúben Louro"), Email("A48926@alunos.isel.pt"), Password("Aa12345!"), Token("1")),
        Admin(2, Username("Luís Reis"), Email("A48318@alunos.isel.pt"), Password("Aa12345!"), Token("2")),
        Admin(3, Username("Pedro Pereira"), Email("palex@cc.isel.ipl.pt"), Password("Aa12345!"), Token("3"))
    )

    /**
     * Function "getUserProfileById" responsible to get the user information when searched by his id.
     * @param userId the id of the user being searched.
     * @return UserProfileInfo? the user profile information if found, null otherwise.
     */
    override fun getUserProfileById(userId: Int): UserInfo? {
        val u = users.find { it.id == userId } ?: return null
        return UserInfo(
            u.id,
            u.token,
            u.username,
            u.email,
            if(u is Admin) Admin.STATE else (u as NormalUser).state.name,
        )
    }

    /**
     * Function "getUserProfileByEmail" responsible to get the user information when searched by his email.
     * @param email the email of the user being searched.
     * @return UserProfileInfo? the user profile information if found, null otherwise.
     */
    override fun getUserProfileByEmail(email: Email): UserInfo? {
        val u = users.find { it.email == email } ?: return null
        return UserInfo(
            u.id,
            u.token,
            u.username,
            u.email,
            if(u is Admin) Admin.STATE else (u as NormalUser).state.name,
        )
    }

    /**
     * Function "getUserProfileByName" responsible to get the user information when searched by his username.
     * @param username the username of the user being searched.
     * @return UserProfileInfo? the user profile information if found, null otherwise.
     */
    override fun getUserProfileByName(username: Username): UserInfo? {
        val u = users.find { it.username == username } ?: return null
        return UserInfo(
            u.id,
            u.token,
            u.username,
            u.email,
            if(u is Admin) Admin.STATE else (u as NormalUser).state.name,
        )
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
        password: Password?,
        state: UserState?
    ): UserInfo {
        val user = getUserFullDetails(userId)!!
        val newName = username ?: user.username
        val newEmail = email ?: user.email
        val newPassword = password ?: user.password

        val updatedUser = if(user is Admin) Admin(user.id, newName, newEmail, newPassword, user.token)
        else NormalUser(user.id, newName, newEmail, newPassword, user.token, state ?: (user as NormalUser).state)
        users.remove(user)
        users.add(updatedUser)
        return UserInfo(
            updatedUser.id,
            updatedUser.token,
            updatedUser.username,
            updatedUser.email,
            if(updatedUser is Admin) Admin.STATE else (updatedUser as NormalUser).state.name,
        )
    }

    /**
     * Function "getUserFullDetails" responsible to get the full details of the user.
     * @param userId the id of the user being searched.
     * @return User? the user if found, null otherwise.
     */
     private fun getUserFullDetails(userId: Int): User? = users.find { it.id == userId }

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
    ): User {
        val lastId = if(users.isEmpty()) 0 else users.maxOf { it.id }

        val newUser = NormalUser(lastId + 1, username, email, password, Token(generateTokenValue()), UserState.NORMAL)
        users.add(newUser)
        return newUser
    }

    /**
     * Responsible for getting a user given a token.
     * @param token The token string of the user to find.
     */
    override fun getUserProfileByToken(token: String): UserInfo? {
        val u = users.find { it.token.value == token } ?: return null
        return UserInfo(
            u.id,
            u.token,
            u.username,
            u.email,
            if(u is Admin) Admin.STATE else (u as NormalUser).state.name,
        )
    }

    /**
     * Responsible for performing the login of a user.
     * @param username The username of the user to log in to.
     * @param password The password of the user to log in to.
     */
    override fun login(username: Username, password: Password): UserInfo? {
        val hashPassword = hashPassword(password.value)
        val u = users.find { it.username == username }!!
        if (u.password.value != hashPassword) return null
        return UserInfo(
            u.id,
            u.token,
            u.username,
            u.email,
            if(u is Admin) Admin.STATE else (u as NormalUser).state.name,
        )
    }

    /**
     * Responsible for getting the users that match a given username sequence.
     * @param username The username sequence to get matches of.
     * @param skip The number of elements to skip.
     * @param limit The maximum number of elements to get.
     */
    override fun getUsersByName(username: String, skip: Int, limit: Int): List<UserInfo> {
        val usersFiltered = users.filter { it.username.value.contains(username) }
        return usersFiltered.subList(skip, minOf(skip + limit, usersFiltered.size))
            .map { UserInfo(it.id, it.token, it.username, it.email, if(it is Admin) Admin.STATE else (it as NormalUser).state.name) }
            .toList()
            .sortedBy { it.username.value }
            .sortedBy { it.email.value }
            .sortedBy { it.state }
            .sortedBy { it.id }
            .toList()
    }
}