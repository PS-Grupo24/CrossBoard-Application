package service

import util.ApiError
import domain.Email
import domain.Password
import domain.Username
import httpModel.UserProfileOutput
import repository.interfaces.UserRepository
import util.Either

class UsersService(private val userRepo: UserRepository) {

     fun createUser(username: Username, email: Email, password: Password):Either<ApiError, UserProfileOutput>{
        if (userRepo.getUserProfileByName(username) != null) return Either.Left(ApiError.USERNAME_ALREADY_EXISTS)
        if (userRepo.getUserProfileByEmail(email) != null) return Either.Left(ApiError.EMAIL_ALREADY_EXISTS)

        return Either.Right(userRepo.addUser(username, email, password))
    }

    fun updateUser(userId: Int, username: Username?, email: Email?, password: Password?):Either<ApiError, UserProfileOutput>{
        if (userRepo.getUserProfileById(userId) == null) return Either.Left(ApiError.USER_NOT_FOUND)
        return Either.Right(userRepo.updateUser(userId, username, email, password))
    }

    fun getUserById(userId: Int):Either<ApiError, UserProfileOutput>{
        val u = userRepo.getUserProfileById(userId) ?: return Either.Left(ApiError.USER_NOT_FOUND)
        return Either.Right(u)
    }
}