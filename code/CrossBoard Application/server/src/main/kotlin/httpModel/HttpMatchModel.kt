package httpModel

import kotlinx.serialization.Serializable
import model.GameType

@Serializable
data class MatchCreation(
    val userId: UInt,
    val gameType: GameType
)