package service

import httpModel.UserProfileInfo
import util.API_Error
import model.Email
import model.Password
import model.Username
import repository.interfaces.UserRepository
import util.Either

class UsersService(private val userRepo: UserRepository) {

     suspend fun createUser(username: Username, email: Email, password: Password):Either<API_Error, UserProfileInfo>{
        if (userRepo.getUserProfileByName(username) != null) return Either.Left(API_Error.USERNAME_ALREADY_EXISTS)
        if (userRepo.getUserProfileByEmail(email) != null) return Either.Left(API_Error.EMAIL_ALREADY_EXISTS)

        return Either.Right(userRepo.addUser(username, email, password))
    }

    suspend fun updateUser(userId: UInt, username: Username?, email: Email?, password: Password?):Either<API_Error, UserProfileInfo>{
        if (userRepo.getUserProfileById(userId) == null) return Either.Left(API_Error.USER_NOT_FOUND)
        return Either.Right(userRepo.updateUser(userId, username, email, password))
    }

    suspend fun getUserById(userId: UInt):Either<API_Error, UserProfileInfo>{
        val u = userRepo.getUserProfileById(userId) ?: return Either.Left(API_Error.USER_NOT_FOUND)
        return Either.Right(u)
    }
}