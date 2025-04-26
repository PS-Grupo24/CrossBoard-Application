package crossBoard.domain

import kotlin.jvm.JvmInline

/**
 * Inline class "Username" represents the username of a user.
 * @param value the username value.
 */
@JvmInline
value class Username (val value: String){

    companion object{
        const val MIN_USERNAME_SIZE = 3
    }

    init {
        require(value.isNotBlank()){"Username can not be blank."}
        require(value.length >= MIN_USERNAME_SIZE){"Username must contain at least 3 letters."}
    }
}

/**
 * Inline class "Email" represents the email of a user.
 * @param value the email value.
 */
@JvmInline
value class Email(val value: String){

    init {
        require(value.isNotBlank()){"Email can not be blank."}
        require(value.contains("@")) {"The email must contain \"@\""}
        require(value.contains(".com") || value.contains(".pt"))
            {"The email must contain in the final \".com\" or \".pt\""}
    }
}

/**
 * Inline class "Password" represents the password of a user.
 * @param value the password value.
 */
@JvmInline
value class Password(val value: String){
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
 * Inline class "Token" represents the token of a user.
 * @param value the token value.
 */
@JvmInline
value class Token(val value: String){
    init {
        require(value.isNotBlank()) {"Token must not be blank."}
    }
}

/**
 * Data class "User" represents a user of the application.
 * @param id the id of the user.
 * @param username the username of the user.
 * @param email the email of the user.
 * @param password the password of the user.
 * @param token the token of the user.
 */
data class User(
    val id:Int,
    val username: Username,
    val email: Email,
    val password: Password,
    val token: Token
){
    init {
        require(id > 0) {"Id must not be greater than 0."}
    }
}
