package service

import httpModel.UserProfileInfo
import util.ApiError
import model.Email
import model.Password
import model.Username
import repository.interfaces.UserRepository
import util.Either

class UsersService(private val userRepo: UserRepository) {

     suspend fun createUser(username: Username, email: Email, password: Password):Either<ApiError, UserProfileInfo>{
        if (userRepo.getUserProfileByName(username) != null) return Either.Left(ApiError.USERNAME_ALREADY_EXISTS)
        if (userRepo.getUserProfileByEmail(email) != null) return Either.Left(ApiError.EMAIL_ALREADY_EXISTS)

        return Either.Right(userRepo.addUser(username, email, password))
    }

    suspend fun updateUser(userId: UInt, username: Username?, email: Email?, password: Password?):Either<ApiError, UserProfileInfo>{
        if (userRepo.getUserProfileById(userId) == null) return Either.Left(ApiError.USER_NOT_FOUND)
        return Either.Right(userRepo.updateUser(userId, username, email, password))
    }

    suspend fun getUserById(userId: UInt):Either<ApiError, UserProfileInfo>{
        val u = userRepo.getUserProfileById(userId) ?: return Either.Left(ApiError.USER_NOT_FOUND)
        return Either.Right(u)
    }
}