package com.crossBoard.util

interface Error

/**
 * The multiple types of errors in the service.
 */
enum class ApiError: Error {
    USER_NOT_FOUND,
    UNAUTHORIZED,
    EMAIL_ALREADY_EXISTS,
    USERNAME_ALREADY_EXISTS,
    USER_ALREADY_IN_MATCH,
    MATCH_NOT_FOUND,
    USER_NOT_IN_THIS_MATCH,
    INCORRECT_PLAYER_TYPE_FOR_THIS_USER,
    VERSION_MISMATCH,
    WRONG_PASSWORD,
    MATCH_NOT_IN_WAITING_STATE,
}