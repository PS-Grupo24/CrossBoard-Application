package com.crossBoard

import com.crossBoard.httpModel.ErrorMessage
import com.crossBoard.httpModel.MatchMessage
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
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds

data class PlayerConnection(
    val userId: Int,
    val matchId: Int,
    val incoming: ReceiveChannel<Frame>,
    val outgoing: SendChannel<Frame>
)
private val activeConnections = ConcurrentHashMap<Int, PlayerConnection>()

fun Application.configureWebSocket(matchService: MatchService, usersService: UsersService) {
    install(WebSockets){
        pingPeriod = 30.seconds
        timeout = 15.seconds
        maxFrameSize = 65536L
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
                                    is Frame.Pong -> {}
                                    is Frame.Text -> {
                                        val message = frame.readText()
                                        println("WS: Received text from user ${user.value.username}: $message")
                                        val opponentId = if (match.value.player1 == user.value.id) match.value.player2 else match.value.player1
                                        when(message){
                                            MatchMessage.MatchOver.message-> {
                                                activeConnections[opponentId]?.outgoing?.send(Frame.Text(MatchMessage.MatchOver.message))
                                                activeConnections.remove(opponentId)
                                                activeConnections.remove(user.value.id)
                                            }
                                            MatchMessage.MatchForfeited.message -> {
                                                activeConnections[opponentId]?.outgoing?.send(Frame.Text(MatchMessage.MatchForfeited.message))
                                                activeConnections.remove(opponentId)
                                                activeConnections.remove(user.value.id)
                                            }
                                            MatchMessage.MatchCancel.message -> activeConnections.remove(user.value.id)
                                            MatchMessage.MoveMade.message -> activeConnections[opponentId]?.outgoing?.send(Frame.Text(message))
                                        }
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
                activeConnections[opponentId]?.outgoing?.send(Frame.Text(MatchMessage.MatchForfeited.message))
                activeConnections.remove(opponentId)
                activeConnections.remove(userId)
            }
            is Failure -> println("Auto-forfeit failed for user $userId in match $matchId: ${forfeit.value}")
        }
    }
}

