package com.crossBoard.domain

import kotlin.jvm.JvmInline

/**
 * Inline value class "Username" represents the username of a user.
 * @param value the username value.
 */
@JvmInline
value class Username (val value: String) {

    companion object{
        const val MIN_USERNAME_SIZE = 3
    }

    init {
        require(value.isNotBlank()) {"Username can not be blank."}
        require(value.length >= MIN_USERNAME_SIZE) {"Username must contain at least 3 letters."}
    }
}

/**
 * Inline value class "Email" represents the email of a user.
 * @param value the email value.
 */
@JvmInline
value class Email(val value: String) {
    init {
        require(value.isNotBlank()){"Email can not be blank."}
        require(value.contains("@")) {"The email must contain \"@\""}
        require(value.contains(".com") || value.contains(".pt"))
            {"The email must contain in the final \".com\" or \".pt\""}
    }
}

/**
 * Inline value class "Password" represents the password of a user.
 * @param value the password value.
 */
@JvmInline
value class Password(val value: String) {
    init {
        require(value.isNotBlank()) {"The password cannot be blank."}
        require(value.length >= 8) {"The password must have at least 8 characters."}
        require(value.matches(Regex(".*[A-Z].*"))) {"The password must have at least one uppercase letter."}
        require(value.matches(Regex(".*[a-z].*"))) {"The password must have at least one lowercase letter."}
        require(value.matches(Regex(".*[0-9].*"))) {"The password must have at least one number."}
        require(value.matches(Regex(".*[!@#\$%^&*].*"))) {"The password must have at least one special character."}
    }
}

/**
 * Inline value class "Token" represents the token of a user.
 * @param value the token value.
 */
@JvmInline
value class Token(val value: String) {
    init {
        require(value.isNotBlank()) {"Token must not be blank."}
    }
}

enum class UserState{
    BANNED, NORMAL
}


interface User{
    val id: Int
    val username: Username
    val email: Email
    val password: Password
    val token: Token
}

/**
 * Data class "User" represents a user of the application.
 * @param id the id of the user.
 * @param username the username of the user.
 * @param email the email of the user.
 * @param password the password of the user.
 * @param token the token of the user.
 */
data class NormalUser(
    override val id:Int,
    override val username: Username,
    override val email: Email,
    override val password: Password,
    override val token: Token,
    val state: UserState
):User{
    init {
        require(id > 0) {"Id must not be greater than 0."}
    }
}

data class Admin(
    override val id: Int,
    override val username: Username,
    override val email: Email,
    override val password: Password,
    override val token: Token,
): User{
    companion object{
        const val STATE = "Admin"
    }
}

data class UserInfo(
    val id: Int,
    val token: Token,
    val username: Username,
    val email: Email,
    val state: String
)