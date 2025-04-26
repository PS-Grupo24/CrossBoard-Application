package crossBoard.httpModel

import kotlinx.serialization.Serializable

@Serializable
data class PlayerOutput(
    val userId: Int?,
    val playerType: String
)
