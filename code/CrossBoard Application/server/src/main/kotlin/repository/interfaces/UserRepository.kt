package repository.interfaces

import domain.Email
import domain.Password
import domain.User
import domain.Username
import httpModel.UserProfileOutput

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
    fun addUser(username: Username, email: Email, password: Password): UserProfileOutput
}