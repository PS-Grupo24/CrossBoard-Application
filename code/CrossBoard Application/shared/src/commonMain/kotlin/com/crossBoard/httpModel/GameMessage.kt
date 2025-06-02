package com.crossBoard.httpModel

import kotlinx.serialization.Serializable

@Serializable
enum class MatchMessage(val message: String) {
    MatchForfeited("Match Forfeited"),
    MatchOver("Match Over"),
    MoveMade("Move Made"),
    MatchCancel("Match Cancelled")
}