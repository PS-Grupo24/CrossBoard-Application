package com.crossBoard.repository.interfaces

import com.crossBoard.domain.Email
import com.crossBoard.domain.Password
import com.crossBoard.domain.User
import com.crossBoard.domain.UserInfo
import com.crossBoard.domain.UserState
import com.crossBoard.domain.Username
import com.crossBoard.httpModel.UserCreationOutput
import com.crossBoard.httpModel.UserLoginOutput
import com.crossBoard.httpModel.UserProfileOutput
import java.security.MessageDigest
import java.util.*

/**
 * Interface UserRepository represents the repository for the user.
 */
interface UserRepository {
    //Function responsible to get the user information when searched by his id.
    fun getUserProfileById(userId:Int): UserInfo?
    //Function responsible to get the user information when searched by his email.
    fun getUserProfileByEmail(email: Email): UserInfo?
    //Function responsible to get the user information when searched by his username.
    fun getUserProfileByName(username: Username): UserInfo?
    //Function responsible to delete the user from the list of users.
    fun deleteUser(userId: Int): Boolean
    //Function responsible to update the user information.
    fun updateUser(userId: Int, username: Username?, email: Email?, password: Password?, userState: UserState?): UserInfo
    //Function responsible to add the user to the list of users.
    fun addUser(username: Username, email: Email, password: Password): User
    //Function responsible to get user details given a token.
    fun getUserProfileByToken(token: String): UserInfo?
    //Function responsible to login a user.
    fun login(username: Username, password: Password): UserInfo?
    //Function responsible to get all users given a username and a skip and limit.
    fun getUsersByName(username: String, skip: Int, limit: Int): List<UserInfo>
}


/**
 * Hashes a password.
 * @param password The password to hash.
 */
fun hashPassword(password: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val bytes = md.digest(password.toByteArray())
    return Base64.getEncoder().encodeToString(bytes)
}

/**
 * Generates a random token.
 */
fun generateTokenValue(): String {
    return UUID.randomUUID().toString()
}