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

/**
 * Responsible for the user management
 * @param userRepo The auxiliary for communication with the database.
 */
class UsersService(private val userRepo: UserRepository) {
    /**
     * Responsible for creating a user.
     * @param username The username of the new user.
     * @param email The email of the new user.
     * @param passwordHash The hashed password of the new user
     */
     fun createUser(username: Username, email: Email, passwordHash: String): Either<ApiError, User> {
        if (userRepo.getUserProfileByName(username) != null) return failure(ApiError.USERNAME_ALREADY_EXISTS)
        if (userRepo.getUserProfileByEmail(email) != null) return failure(ApiError.EMAIL_ALREADY_EXISTS)

        return success(userRepo.addUser(username, email, passwordHash))
    }

    /**
     * Responsible for the login verification.
     * @param username The username of the user to login.
     * @param passwordHash The hashed password of the user to login.
     */
    fun login(username: Username, passwordHash: String): Either<ApiError, UserInfo> {
        if (userRepo.getUserProfileByName(username) == null) return failure(ApiError.USER_NOT_FOUND)
        return success(userRepo.login(username, passwordHash) ?: return failure(ApiError.WRONG_PASSWORD))
    }

    /**
     * Responsible for updating the information of a user.
     * @param userId The id of the user to update.
     * @param username The new username for the user or null if not to change.
     * @param email The new email for the user or null if not to change.
     * @param password The new password for the user or null if not to change.
     * @param state The new state for the user or null if not to change
     */
    fun updateUser(
        userId: Int,
        username: Username? = null,
        email: Email? = null,
        password: String? = null,
        state: UserState? = null
    ): Either<ApiError, UserInfo> {
        if (userRepo.getUserProfileById(userId) == null) return failure(ApiError.USER_NOT_FOUND)
        return success(userRepo.updateUser(userId, username, email, password, state))
    }

    /**
     * Responsible to find a user given an id.
     * @param userId The id of the user to find.
     */
    fun getUserById(userId: Int): Either<ApiError, UserInfo> {
        val u = userRepo.getUserProfileById(userId) ?: return failure(ApiError.USER_NOT_FOUND)
        return success(u)
    }

    /**
     * Responsible to find a user given a token.
     * @param userToken The token of the user to find.
     */
    fun getUserByToken(userToken: String): Either<ApiError, UserInfo> {
        val u = userRepo.getUserProfileByToken(userToken) ?: return failure(ApiError.USER_NOT_FOUND)
        return success(u)
    }

    /**
     * Responsible to find users whose name matches with a sequence.
     * @param username The username sequence to filter by.
     * @param skip The number of elements to skip.
     * @param limit The maximum number of elements to return.
     */
    fun getUsersByName(username: String, skip: Int, limit: Int): List<UserInfo> {
        return userRepo.getUsersByName(username,  skip, limit)
    }
}