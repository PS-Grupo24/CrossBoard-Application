package com.crossBoard

import com.crossBoard.domain.MatchState
import com.crossBoard.httpModel.ErrorMessage
import com.crossBoard.httpModel.GameMessage
import com.crossBoard.service.MatchService
import com.crossBoard.service.UsersService
import com.crossBoard.util.Failure
import com.crossBoard.util.Success
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds

private val activeConnections = ConcurrentHashMap<Int, PlayerConnection>()

fun Application.configureWebSocket(matchService: MatchService, usersService: UsersService) {
    install(WebSockets){
        pingPeriod = 15.seconds
        timeout = 30.seconds
    }
    routing {
        webSocket("/match-ws") {
            val userToken = call.request.headers["Authorization"]?.removePrefix("Bearer ")

            if (userToken == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No match or user ID found"))
                return@webSocket
            }

            when (val user = usersService.getUserByToken(userToken)) {
                is Success -> {
                    val match = matchService.getRunningMatch(user.value.id)
                    when (match) {
                        is Success -> {

                            activeConnections[user.value.id] = PlayerConnection(
                                user.value.id,
                                match.value.id,
                                incoming,
                                outgoing
                            )

                            for (frame in incoming) {
                                when (frame) {
                                    is Frame.Pong -> println("WS: Received pong from user ${user.value.username}")
                                    is Frame.Text -> {
                                        val message = frame.readText()
                                        println("WS: Received text from user ${user.value.username}: $message")
                                    }

                                    is Frame.Binary -> {}
                                    is Frame.Close -> {}
                                    is Frame.Ping -> {}
                                }
                            }
                        }

                        is Failure -> {
                            println("WS: User ${user.value.username} connected but not in match.")
                        }
                    }

                    println("User ${user.value.username} disconnected")
                    activeConnections.remove(user.value.id)
                    if (match is Success && match.value.state == MatchState.RUNNING) {
                        println("User ${user.value.username} disconnected mid match ${match.value.id}.")

                        triggerAutoForfeit(match.value.id, user.value.id, matchService)
                    }
                }

                is Failure -> {
                    return@webSocket call.respond(HttpStatusCode.NotFound, ErrorMessage("User not found"))
                }
            }
        }
    }
}

fun triggerAutoForfeit(matchId: Int, userId: Int, matchService: MatchService) {
    CoroutineScope(Dispatchers.IO).launch {
        when (val forfeit = matchService.forfeit(matchId, userId)) {
            is Success -> {
                val match = forfeit.value
                println("Auto-forfeit successful for user $userId in match $matchId.")
                val opponentId = if (match.player1 == userId) match.player2 else match.player1
                if (opponentId != null) {
                    val json = Json.encodeToString(GameMessage.MatchForfeit(matchId, "Opponent Forfeited"))
                    activeConnections[opponentId]?.outgoing?.send(Frame.Text(json))
                }
            }

            is Failure -> println("Auto-forfeit failed for user $userId in match $matchId: ${forfeit.value}")
        }
    }
}

data class PlayerConnection(
    val userId: Int,
    val matchId: Int,
    val incoming: ReceiveChannel<Frame>,
    val outgoing: SendChannel<Frame>
)