package com.crossBoard

import com.crossBoard.httpModel.ErrorMessage
import com.crossBoard.service.UsersService
import com.crossBoard.util.Failure
import com.crossBoard.util.Success
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import io.ktor.server.sse.SSE
import io.ktor.server.sse.ServerSSESession
import io.ktor.server.sse.heartbeat
import io.ktor.server.sse.sse
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.awaitCancellation
import kotlinx.serialization.serializer
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.milliseconds

/**
 * The user sessions using ConcurrentHashMap to store the ServerSSESession.
 */
private val userEventSessions = ConcurrentHashMap<Int, ServerSSESession>()

/**
 * Configure the Server-Sent Events for the server.
 * @param usersService The service for user handling.
 */
fun Application.configureServerSentEvents(usersService: UsersService) {
    install(SSE)

    routing{
        sse("/events", serialize = { typeInfo, it ->
            val serializer = json.serializersModule.serializer(typeInfo.kotlinType!!)
            json.encodeToString(serializer, it)
        }){

            heartbeat {
                period = 10.milliseconds
                event = ServerSentEvent(event = "heartbeat")
            }
            val userToken = call.request.headers["Authorization"]?.removePrefix("Bearer ")

            if (userToken == null) {
                println("SSE: Connection attempt for user with missing token.")
                call.respond(HttpStatusCode.Unauthorized, ErrorMessage("Missing token"))
                return@sse
            }

            when(val userResult = usersService.getUserByToken(userToken)){
                is Success -> {
                    val user = userResult.value

                    try {
                        userEventSessions.putIfAbsent(user.id, this)
                        println("SSE: Session active for user ${user.username.value}. Sending events from channel.")
                        awaitCancellation()
                    }
                    catch (e: Exception){
                        println("SSE: Error in connection for user ${user.username.value}: ${e.message}")
                    }
                }
                is Failure -> {
                    println("SSE: Connection attempt for user with invalid token. Failure: ${userResult.value}")
                    handleFailure(call, userResult.value)
                    return@sse
                }
            }
        }
    }
}

/**
 * Responsible to send an event through a session.
 * @param userId The user id of which the session belongs to.
 * @param event The event to send.
 */
suspend fun sendEventToUser(userId: Int, event: ServerSentEvent) {
    try {
        println("Sending event: ${event.event}")
        userEventSessions[userId]?.send(event)
    }
    catch (e: Exception){
        println("SSE: Error while sending event: ${e.message}")
        e.printStackTrace()
    }

}

/**
 * Responsible to close a session.
 * @param userId The user id of which the session to close belongs to.
 */
suspend fun disconnectUserEvent(userId: Int) {
    println("Attempting to disconnect user $userId's SSE channel.")
    val session = userEventSessions.remove(userId)
    session?.close()
    if (session != null) {
        println("SSE: Channel closed and removed for user $userId.")
    } else {
        println("SSE: No active channel found for user $userId to disconnect.")
    }
}