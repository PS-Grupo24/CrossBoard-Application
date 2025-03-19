package httpModel

import kotlinx.serialization.Serializable
import model.Email
import model.Password
import model.Username


/**
 * Data class "UserCreation" represents the data format for the user creation.
 * @param username the username of the new user.
 * @param email the email of the new user.
 * @param password the password of the new user.
 */
@Serializable
data class UserCreation(
    val username: Username,
    val email: Email,
    val password: Password
)

/**
 * Data class "UserProfileInfo" represents the profile data of the user.
 * @param username the username of the new user.
 * @param email the email of the new user.
 * @param id the id of the new user.
 */
@Serializable
data class UserProfileInfo(
    val id:UInt,
    val username: Username,
    val email: Email,
)

/**
 * Data class "UserUpdate" represents the data format for user update.
 * @param username the username of the new user.
 * @param email the email of the new user.
 * @param password the password of the new user.
 */
@Serializable
data class UserUpdate(
    val username: Username?,
    val email: Email?,
    val password: Password?
)