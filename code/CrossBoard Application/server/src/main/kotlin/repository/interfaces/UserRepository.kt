package repository.interfaces

import httpModel.UserProfileInfo
import domain.Email
import domain.Password
import domain.User
import domain.Username

/**
 * Interface UserRepository represents the repository for the user.
 */
interface UserRepository {
    //Function responsible to get the user information when searched by his id.
    fun getUserProfileById(userId:UInt): UserProfileInfo?
    //Function responsible to get the user information when searched by his email.
    fun getUserProfileByEmail(email: Email): UserProfileInfo?
    //Function responsible to get the user information when searched by his username.
    fun getUserProfileByName(username: Username): UserProfileInfo?
    //Function responsible to delete the user from the list of users.
    fun deleteUser(userId: UInt): Boolean
    //Function responsible to update the user information.
    fun updateUser(userId: UInt, username: Username?, email: Email?, password: Password?): UserProfileInfo
    //Function responsible to get the user full details.
    fun getUserFullDetails(userId: UInt): User?
    //Function responsible to add the user to the list of users.
    fun addUser(username: Username, email: Email, password: Password): UserProfileInfo
}