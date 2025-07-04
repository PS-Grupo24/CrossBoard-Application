package com.crossBoard.repository.interfaces

import com.crossBoard.domain.Email
import com.crossBoard.domain.User
import com.crossBoard.domain.UserInfo
import com.crossBoard.domain.UserState
import com.crossBoard.domain.Username

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
    fun updateUser(userId: Int, username: Username?, email: Email?, passwordHash: String?, userState: UserState?): UserInfo
    //Function responsible to add the user to the list of users.
    fun addUser(username: Username, email: Email, passwordHash: String): User
    //Function responsible to get user details given a token.
    fun getUserProfileByToken(token: String): UserInfo?
    //Function responsible to log in a user.
    fun login(username: Username, passwordHash: String): UserInfo?
    //Function responsible to get all users given a username and a skip and limit.
    fun getUsersByName(username: String, skip: Int, limit: Int): List<UserInfo>
}

/**
 * Generates a random token.
 */
fun generateTokenValue(): String {
    return UUID.randomUUID().toString()
}