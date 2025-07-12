package com.crossBoard.callReceiver

import com.crossBoard.domain.MatchType
import com.crossBoard.httpModel.moveHttp.MoveHttp
import io.ktor.server.routing.RoutingCall

/**
 * Interface `CallReceiver` is responsible for abstracting the process of receiving a [MoveHttp] from a Ktor [RoutingCall].
 *
 * This is particularly useful in a Kotlin Multiplatform project where server-side input parsing logic
 * (e.g., deserializing a player's move from an HTTP request) needs to be modular and match-type-specific.
 *
 * Each implementation of [CallReceiver] should handle deserialization logic for its associated [MatchType].
 *
 * @param MH The type of [MoveHttp] expected for the match.
 *
 * Properties:
 * - [matchType]: Identifies which game/match this receiver is associated with.
 *
 * Functions:
 * - [callReceive]: A suspend function that extracts and returns a [MoveHttp] from the provided [RoutingCall].
 */
interface CallReceiver<MH: MoveHttp> {

    val matchType: MatchType

    /**
     * Receives and parses a [MoveHttp] from the given Ktor [RoutingCall].
     *
     * @param call The HTTP call context from Ktor's routing.
     * @return The deserialized [MoveHttp] instance.
     */
    suspend fun callReceive(call: RoutingCall): MH
}