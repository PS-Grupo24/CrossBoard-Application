package org.example.project.model

import kotlin.jvm.JvmInline

const val MIN_USERNAME_SIZE = 3

@JvmInline
value class Username (val value: String){

    init {
        require(value.isNotBlank()){"Username can not be blank."}
        require(value.length >= MIN_USERNAME_SIZE){"Username must contain at least 3 letters."}
    }
}

@JvmInline
value class Email(val value: String){

    init {
        require(value.isNotBlank()){"Email can not be blank."}
        require(value.contains("@")) {"The email must contain \"@\""}
        require(value.contains(".com") || value.contains(".pt"))
            {"The email must contain in the final \".com\" or \".pt\""}
    }
}

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

data class User(
    val id:UInt,
    val userName:Username,
    val email:Email,
    val password:Password
){
    init {
        require(id > 0U) {"Id must not be greater than 0."}
    }
}