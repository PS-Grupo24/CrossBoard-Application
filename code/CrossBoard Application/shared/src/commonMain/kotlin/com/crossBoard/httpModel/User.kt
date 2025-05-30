package com.crossBoard.httpModel

import kotlinx.serialization.Serializable


/**
 * Data class "UserCreationInput" represents the data format for the user creation.
 * @param username the username of the new user.
 * @param email the email of the new user.
 * @param password the password of the new user.
 */
@Serializable
data class UserCreationInput(
    val username: String,
    val email: String,
    val password: String
)

/**
 * Data class "UserCreationOutput" represents the data format for the user creation output.
 * @param id The id of the created user.
 * @param token The token of the created user.
 */
@Serializable
data class UserCreationOutput(
    val id: Int,
    val token: String,
)


/**
 * Data class "UserProfileInfo" represents the profile data of the user.
 * @param username the username of the new user.
 * @param email the email of the new user.
 * @param id the id of the new user.
 */
@Serializable
data class UserProfileOutput(
    val id:Int,
    val username: String,
    val email: String,
    val token: String,
    val state: String
)

/**
 * Data class "UserUpdate" represents the data format for user update.
 * @param username the username of the new user.
 * @param email the email of the new user.
 * @param password the password of the new user.
 */
@Serializable
data class UserUpdateInput(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
)


/**
 * Data class "UserLoginInput" represents the data format to login.
 * @param username The username of the target user to login.
 * @param password The password of the target username.
 */
@Serializable
data class UserLoginInput(
    val username: String,
    val password: String
)

@Serializable
data class UserLoginOutput(
    val id:Int,
    val token: String,
    val email: String,
    val state: String
)