package com.crossBoard.repository.interfaces

import com.crossBoard.domain.Email
import com.crossBoard.domain.Password
import com.crossBoard.domain.User
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
    fun getUserProfileById(userId:Int): UserProfileOutput?
    //Function responsible to get the user information when searched by his email.
    fun getUserProfileByEmail(email: Email): UserProfileOutput?
    //Function responsible to get the user information when searched by his username.
    fun getUserProfileByName(username: Username): UserProfileOutput?
    //Function responsible to delete the user from the list of users.
    fun deleteUser(userId: Int): Boolean
    //Function responsible to update the user information.
    fun updateUser(userId: Int, username: Username?, email: Email?, password: Password?): UserProfileOutput
    //Function responsible to get the user full details.
    fun getUserFullDetails(userId: Int): User?
    //Function responsible to add the user to the list of users.
    fun addUser(username: Username, email: Email, password: Password): UserCreationOutput
    //Function responsible to get user details given a token.
    fun getUserProfileByToken(token: String): UserProfileOutput?
    //Function responsible to login a user.
    fun login(username: Username, password: Password): UserLoginOutput?
}

fun hashPassword(password: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val bytes = md.digest(password.toByteArray())
    return Base64.getEncoder().encodeToString(bytes)
}

fun generateTokenValue(): String {
    return UUID.randomUUID().toString()
}