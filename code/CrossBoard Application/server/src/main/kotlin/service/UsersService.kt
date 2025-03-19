package service

import httpModel.UserProfileInfo
import model.Email
import model.Password
import model.Username
import repository.interfaces.UserRepository

class UsersService(private val userRepo: UserRepository) {
     suspend fun createUser(username: Username, email: Email, password: Password):UserProfileInfo{
        if (userRepo.getUserProfileByName(username) != null) throw IllegalArgumentException("Username already exists")
        if (userRepo.getUserProfileByEmail(email) != null) throw IllegalArgumentException("Email already exists")

        return userRepo.addUser(username, email, password)
    }

    suspend fun updateUser(userId: UInt, username: Username?, email: Email?, password: Password?):UserProfileInfo{
        if (userRepo.getUserProfileById(userId) == null) throw IllegalArgumentException("This user does not exist")
        return userRepo.updateUser(userId, username, email, password)
    }

    suspend fun getUserById(userId: UInt):UserProfileInfo{
        val u = userRepo.getUserProfileById(userId) ?: throw IllegalArgumentException("This user does not exist")
        return u
    }
}