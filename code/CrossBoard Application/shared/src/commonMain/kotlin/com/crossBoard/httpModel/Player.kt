package com.crossBoard.httpModel

import kotlinx.serialization.Serializable

/**
 * Data class PlayerOutput to represent the information of the player in a match for a response.
 * @param userId The id of the user in the match.
 * @param playerType The corresponding player type of the user in the match.
 */
@Serializable
data class PlayerOutput(
    val userId: Int?,
    val playerType: String
)
