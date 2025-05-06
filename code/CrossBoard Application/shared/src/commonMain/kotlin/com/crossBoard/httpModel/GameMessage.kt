package com.crossBoard.httpModel

import kotlinx.serialization.Serializable

@Serializable
sealed class GameMessage {
    data object Ping : GameMessage()
    data object Pong : GameMessage()
    //data class RematchOffer() : GameMessage()
    //data class GameStateUpdate() : GameMessage()
    data class MatchForfeit(val matchId: Int, val message: String) : GameMessage()
}