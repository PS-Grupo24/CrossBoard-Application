package httpModel

import kotlinx.serialization.Serializable
import domain.GameType

@Serializable
data class MatchCreation(
    val userId: UInt,
    val gameType: GameType
)