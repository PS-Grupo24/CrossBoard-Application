package httpModel

import kotlinx.serialization.Serializable


/**
 * Data class "UserCreation" represents the data format for the user creation.
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
    val password: String? = null
)