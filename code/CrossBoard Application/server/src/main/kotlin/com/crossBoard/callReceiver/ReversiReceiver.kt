package com.crossBoard.callReceiver

import com.crossBoard.domain.MatchType
import com.crossBoard.httpModel.moveInput.ReversiMoveInput
import io.ktor.server.request.receive
import io.ktor.server.routing.RoutingCall

/**
 * Implementation of [CallReceiver] for the Reversi game.
 *
 * This class is responsible for receiving and deserializing a [ReversiMoveInput] from a Ktor [RoutingCall].
 * It ensures that when a Reversi match is being played, the correct input format is extracted from the request body.
 *
 * Used in dynamic input resolution based on [MatchType.Reversi].
 */
class ReversiReceiver : CallReceiver<ReversiMoveInput> {
    override val matchType = MatchType.Reversi
    override suspend fun callReceive(call: RoutingCall): ReversiMoveInput {
        return call.receive<ReversiMoveInput>()
    }
}