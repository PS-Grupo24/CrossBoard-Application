package util

interface Error

enum class API_Error: Error {
    USER_NOT_FOUND,
    UNAUTHORIZED,
    EMAIL_ALREADY_EXISTS,
    USERNAME_ALREADY_EXISTS
}