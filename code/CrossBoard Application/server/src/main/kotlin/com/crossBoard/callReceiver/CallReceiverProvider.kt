package com.crossBoard.callReceiver

import com.crossBoard.domain.MatchType
import com.crossBoard.httpModel.moveHttp.MoveHttp
import io.ktor.server.routing.RoutingCall
import kotlin.IllegalArgumentException

object CallReceiverProvider {
    /**
     * List of all available [CallReceiver] implementations for each supported match type.
     *
     * Each receiver in this list is responsible for handling the deserialization of a [MoveHttp]
     * from a Ktor [RoutingCall] specific to its game (e.g., Tic-Tac-Toe, Reversi).
     *
     * This collection can be used to dynamically select the appropriate receiver based on the [MatchType],
     * allowing for flexible and scalable support of multiple games.
     *
     * Add new receivers to this list when introducing new match types.
     */
    private val callReceivers = listOf(
        TicReceiver(),
        ReversiReceiver()
    )

    private val callReceiversMap: Map<MatchType, CallReceiver<*>> by lazy {
        callReceivers.associateBy { it.matchType }
    }

    @Suppress("UNCHECKED_CAST")
    fun get(matchType: MatchType) : CallReceiver<*> {
        return (callReceiversMap[matchType]
            ?: throw IllegalArgumentException("No UIModule found for MatchType: $matchType")) as CallReceiver<MoveHttp>
    }
}