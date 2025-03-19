package repository.interfaces

import httpModel.UserProfileInfo
import model.Email
import model.Password
import model.User
import model.Username

interface UserRepository {
    suspend fun getUserProfileById(userId:UInt): UserProfileInfo?
    suspend fun getUserProfileByEmail(email: Email): UserProfileInfo?
    suspend fun getUserProfileByName(username: Username): UserProfileInfo?
    suspend fun getUserFullDetails(userId: UInt): User?
    suspend fun deleteUser(userId: UInt): Boolean
    suspend fun updateUser(userId: UInt, username: Username?, email: Email?, password: Password?): UserProfileInfo
    suspend fun addUser(username: Username, email: Email, password: Password): UserProfileInfo
}