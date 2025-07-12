package com.crossBoard.callReceiver

import com.crossBoard.domain.MatchType
import com.crossBoard.httpModel.moveHttp.TicTacToeMoveHttp
import io.ktor.server.request.receive
import io.ktor.server.routing.RoutingCall

/**
 * Implementation of [CallReceiver] for the Tic-Tac-Toe game.
 *
 * This class handles the deserialization of [com.crossBoard.httpModel.moveHttp.TicTacToeMoveHttp] from a Ktor [RoutingCall].
 * It ensures that when a Tic-Tac-Toe match is being played, the input payload is properly parsed.
 *
 * Used in conjunction with the match type [MatchType.TicTacToe] to dynamically route input handling logic.
 */
class TicReceiver : CallReceiver<TicTacToeMoveHttp> {
    override val matchType = MatchType.TicTacToe
    override suspend fun callReceive(call: RoutingCall): TicTacToeMoveHttp {
        return call.receive<TicTacToeMoveHttp>()
    }
}