package com.crossBoard.service

import com.crossBoard.util.ApiError
import com.crossBoard.domain.Email
import com.crossBoard.domain.Password
import com.crossBoard.domain.User
import com.crossBoard.domain.UserInfo
import com.crossBoard.domain.UserState
import com.crossBoard.domain.Username
import com.crossBoard.repository.interfaces.UserRepository
import com.crossBoard.util.Either
import com.crossBoard.util.failure
import com.crossBoard.util.success

class UsersService(private val userRepo: UserRepository) {

     fun createUser(username: Username, email: Email, password: Password): Either<ApiError, User> {
        if (userRepo.getUserProfileByName(username) != null) return failure(ApiError.USERNAME_ALREADY_EXISTS)
        if (userRepo.getUserProfileByEmail(email) != null) return failure(ApiError.EMAIL_ALREADY_EXISTS)

        return success(userRepo.addUser(username, email, password))
    }

    fun login(username: Username, password: Password): Either<ApiError, UserInfo> {
        if (userRepo.getUserProfileByName(username) == null) return failure(ApiError.USER_NOT_FOUND)
        return success(userRepo.login(username, password) ?: return failure(ApiError.WRONG_PASSWORD))
    }

    fun updateUser(
        userId: Int,
        username: Username? = null,
        email: Email? = null,
        password: Password? = null,
        state: UserState? = null
    ): Either<ApiError, UserInfo> {
        if (userRepo.getUserProfileById(userId) == null) return failure(ApiError.USER_NOT_FOUND)
        return success(userRepo.updateUser(userId, username, email, password, state))
    }

    fun getUserById(userId: Int): Either<ApiError, UserInfo> {
        val u = userRepo.getUserProfileById(userId) ?: return failure(ApiError.USER_NOT_FOUND)
        return success(u)
    }

    fun getUserByToken(userToken: String): Either<ApiError, UserInfo> {
        val u = userRepo.getUserProfileByToken(userToken) ?: return failure(ApiError.USER_NOT_FOUND)
        return success(u)
    }

    fun getUsersByName(username: String, skip: Int, limit: Int): List<UserInfo> {
        return userRepo.getUsersByName(username,  skip, limit)
    }
}