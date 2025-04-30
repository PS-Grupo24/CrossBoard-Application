package com.crossBoard.service

import com.crossBoard.util.ApiError
import com.crossBoard.domain.Email
import com.crossBoard.domain.Password
import com.crossBoard.domain.Username
import com.crossBoard.httpModel.UserCreationOutput
import com.crossBoard.httpModel.UserLoginOutput
import com.crossBoard.httpModel.UserProfileOutput
import com.crossBoard.repository.interfaces.UserRepository
import com.crossBoard.util.Either

class UsersService(private val userRepo: UserRepository) {

     fun createUser(username: Username, email: Email, password: Password): Either<ApiError, UserCreationOutput> {
        if (userRepo.getUserProfileByName(username) != null) return Either.Left(ApiError.USERNAME_ALREADY_EXISTS)
        if (userRepo.getUserProfileByEmail(email) != null) return Either.Left(ApiError.EMAIL_ALREADY_EXISTS)

        return Either.Right(userRepo.addUser(username, email, password))
    }

    fun login(username: Username, password: Password): Either<ApiError, UserLoginOutput> {
        if (userRepo.getUserProfileByName(username) == null) return Either.Left(ApiError.USER_NOT_FOUND)
        return Either.Right(userRepo.login(username, password) ?: return Either.Left(ApiError.WRONG_PASSWORD))
    }

    fun updateUser(userId: Int, username: Username?, email: Email?, password: Password?): Either<ApiError, UserProfileOutput> {
        if (userRepo.getUserProfileById(userId) == null) return Either.Left(ApiError.USER_NOT_FOUND)
        return Either.Right(userRepo.updateUser(userId, username, email, password))
    }

    fun getUserById(userId: Int): Either<ApiError, UserProfileOutput> {
        val u = userRepo.getUserProfileById(userId) ?: return Either.Left(ApiError.USER_NOT_FOUND)
        return Either.Right(u)
    }

    fun getUserByToken(userToken: String): Either<ApiError, UserProfileOutput> {
        val u = userRepo.getUserProfileByToken(userToken) ?: return Either.Left(ApiError.USER_NOT_FOUND)
        return Either.Right(u)
    }
}