package com.crossBoard

import com.crossBoard.domain.Email
import com.crossBoard.domain.MultiPlayerMatch
import com.crossBoard.domain.Token
import com.crossBoard.domain.UserInfo
import com.crossBoard.domain.UserState
import com.crossBoard.domain.Username
import com.crossBoard.httpModel.ErrorMessage
import com.crossBoard.httpModel.EventType
import com.crossBoard.httpModel.MatchCancel
import com.crossBoard.httpModel.MatchOutput
import com.crossBoard.httpModel.MatchPlayedOutput
import com.crossBoard.httpModel.MatchStats
import com.crossBoard.httpModel.UserCreationInput
import com.crossBoard.httpModel.UserCreationOutput
import com.crossBoard.httpModel.UserLoginInput
import com.crossBoard.httpModel.UserLoginOutput
import com.crossBoard.httpModel.UserProfileOutput
import com.crossBoard.httpModel.MoveInput
import com.crossBoard.httpModel.toMultiplayerMatch
import com.crossBoard.interfaces.Clearable
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.network.*
import kotlinx.serialization.SerializationException
import com.crossBoard.util.Either
import com.crossBoard.util.failure
import com.crossBoard.util.success
import com.crossBoard.utils.clientJson
import io.ktor.client.plugins.sse.sse
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

/**
 * class ApiClient, responsible for the requests to the server
 */
class ApiClient(
    private val client: HttpClient,
    val host: Host,
    private val apiScope: CoroutineScope = CoroutineScope(SupervisorJob())
): Clearable {

    /**
     * Base URL where the server is hosted.
     */
    private val baseUrl = "http://${host.address}:${host.port}"

    /**
     * Function "banUser", responsible for the request to ban a user.
     * @param userToken The token of the admin performing the ban.
     * @param userId The id of the user to forfeit.
     * @return `String` with the error message on failure; `UserInfo` on Success.
     */
    suspend fun banUser(userToken: String, userId: Int): Either<String, UserInfo> {
        val response = try {
            client.post("$baseUrl/user/$userId/ban") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $userToken")
            }
        }
        catch (e: UnresolvedAddressException) {
            return failure(e.message ?: "No internet connection")
        }
        catch (e: Exception){
            return failure(e.message ?: "Something went wrong")
        }
        return if (response.status.value in 200 .. 299){
            val body = response.body<UserProfileOutput>()
            success(UserInfo(body.id, Token(body.token), Username(body.username), Email(body.email), body.state))

        }
        else {
            val error = response.body<ErrorMessage>()
            failure(error.message)
        }
    }

    /**
     * Function "unbanUser" responsible for unbanning a user.
     * @param userToken The token of the admin performing the unban.
     * @param userId The id of the user to unban.
     * @return `String` with the error message on failure; `UserInfo` on Success.
     */
    suspend fun unbanUser(userToken: String, userId: Int): Either<String, UserInfo> {
        val response = try {
            client.post("$baseUrl/user/$userId/unban") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $userToken")
            }
        }
        catch (e: UnresolvedAddressException) {
            return failure(e.message ?: "No internet connection")
        }
        catch (e: Exception){
            return failure(e.message ?: "Something went wrong")
        }
        return if (response.status.value in 200 .. 299){
            val body = response.body<UserProfileOutput>()
            success(UserInfo(body.id, Token(body.token), Username(body.username), Email(body.email), body.state))
        }
        else {
            val error = response.body<ErrorMessage>()
            failure(error.message)
        }
    }

    /**
     * Function "getUsersByName" responsible for getting a list of users that match a username sequence.
     * @param userToken The token of the user performing the request.
     * @param username The username sequence to match.
     * @param skip The number of elements to skip; `NULL` if skipping not wanted.
     * @param limit The maximum number of elements to get. `NULL` for no maximum.
     * @return `String` with the error message on failure; `List<UserInfo>` on Success.
     */
    suspend fun getUsersByName(userToken: String, username: String, skip: Int? = null, limit: Int? = null): Either<String, List<UserInfo>> {
        val response = try {
            client.get("$baseUrl/user/username/$username") {
                parameter("skip", skip)
                parameter("limit", limit)
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $userToken")
            }
        }
        catch (e: UnresolvedAddressException) {
            return failure(e.message ?: "No internet connection")
        }
        catch (e: Exception){
            return failure(e.cause?.message ?: e.message ?: "Something went wrong")
        }

        return if (response.status.value in 200 .. 299){
            val users = response.body<List<UserProfileOutput>>().map {
                UserInfo(
                    it.id,
                    Token(it.token),
                    Username(it.username),
                    Email(it.email),
                    it.state
                )
            }
            success(users)
        }
        else {
            val error = response.body<ErrorMessage>()
            failure(error.message)
        }
    }

    /**
     * Responsible for performing the log in for a user.
     * @param username The username of the user to log in to.
     * @param password The password of the user to log in to.
     * @return `String` with the error message on failure; `UserInfo` on Success.
     */
    suspend fun login(username: String, password: String): Either<String, UserInfo> {
        val response = try {
            println(baseUrl)
            client.post("$baseUrl/user/login") {
                contentType(ContentType.Application.Json)
                setBody(UserLoginInput(username, password))
            }
        }
        catch (e: UnresolvedAddressException) {
            return failure(e.message ?: "No internet connection")
        }
        catch (e: Exception){
            return failure(e.cause?.message ?: e.message ?: "Something went wrong")
        }

        return if (response.status.value in 200 .. 299){
            val logged = response.body<UserLoginOutput>()
            success(
                UserInfo(
                    logged.id,
                    Token(logged.token),
                    Username(username),
                    Email(logged.email),
                    logged.state
                )
            )
        }
        else {
            val error = response.body<ErrorMessage>()
            failure(error.message)
        }
    }

    /**
     * Responsible for creating a new user.
     * @param username The username for the new user.
     * @param email The email for the new user.
     * @param password The password for the new user.
     * @return `String` with the error message on failure; `UserInfo` on Success.
     */
    suspend fun register(username: String, email: String, password: String): Either<String, UserInfo> {
        val response = try {
            client.post("$baseUrl/user") {
                contentType(ContentType.Application.Json)
                setBody(UserCreationInput(username, email, password))
            }
        }
        catch (e: UnresolvedAddressException) {
            return failure(e.message ?: "No internet connection")
        }
        catch (e: Exception){
            return failure(e.cause?.message ?: e.message ?: "Something went wrong")
        }

        return if (response.status.value in 200 .. 299){
            val registered = response.body<UserCreationOutput>()
            success(
                UserInfo(
                    registered.id,
                    Token(registered.token),
                    Username(username),
                    Email(email),
                    UserState.NORMAL.name,
                )
            )
        }
        else {
            val error = response.body<ErrorMessage>()
            failure(error.message)
        }
    }

    /**
     * Responsible for getting a user by its id.
     * @param userId The id of the user to find.
     * @return `String` with the error message on failure; `UserInfo` on Success.
     */
    suspend fun getUserById(userId:Int): Either<String, UserInfo> {
        val response = try {
            client.get("$baseUrl/user/$userId"){
                contentType(ContentType.Application.Json)
            }
        }
        catch (e: UnresolvedAddressException){
            return failure(e.message ?: "No internet connection")
        }
        catch (e: Exception){
            return failure(e.message ?: "Something went wrong")
        }

        return if (response.status.value in 200 .. 299){
            val user = response.body<UserProfileOutput>()
            success(
                UserInfo(
                    user.id,
                    Token(user.token),
                    Username(user.username),
                    Email(user.email),
                    UserState.NORMAL.name,
                )
            )
        }
        else {
            val error = response.body<ErrorMessage>()
            failure(error.message)
        }
    }

    /**
     * Responsible for performing a request to the server to enter a match.
     * @param userToken The token of the user to join a match.
     * @param matchType The type of match to join to.
     * @return `String` with the error message on failure; `MultiPlayerMatch` on Success.
     */
    suspend fun enterMatch(userToken: String, matchType: String): Either<String, MultiPlayerMatch> {
        val response = try {
            client.post(
                urlString = "$baseUrl/match/$matchType",
            ){
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $userToken")
            }
        }
        catch (e: SerializationException) {
            return failure(e.message ?: "Serialization exception")
        }
        catch (e: UnresolvedAddressException) {
            return failure(e.message ?: "No internet connection")
        }

        return if (response.status.value in 200 .. 299){
            val match = response.body<MatchOutput>()
            success(match.toMultiplayerMatch())
        }
        else {
            val error = response.body<ErrorMessage>()
            failure(error.message)
        }
    }

    /**
     * Responsible for performing a request to the server to forfeit a match.
     * @param userToken The token of the user performing the forfeit.
     * @param matchId The id of the match to forfeit.
     * @return `String` with the error message on failure; `MultiPlayerMatch` on Success.
     */
    suspend fun forfeitMatch(userToken: String, matchId: Int): Either<String, MultiPlayerMatch>
    {
        val response = try {
            client.post(
                urlString = "$baseUrl/match/$matchId/forfeit",
            ){
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $userToken")
            }
        }
        catch (e: SerializationException) {
            return failure(e.message ?: "Serialization exception")
        }
        catch (e: UnresolvedAddressException) {
            return failure(e.message ?: "No internet connection")
        }

        return if (response.status.value in 200 .. 299){
            val forfeitedMatch = response.body<MatchOutput>()
            success(forfeitedMatch.toMultiplayerMatch())
        }
        else {
            val error = response.body<ErrorMessage>()
            failure(error.message)
        }
    }

    /**
     * Responsible for performing a request to the server to make a play in a match.
     * @param userToken The token of the user performing the play.
     * @param matchId The id of the match to perform a play.
     * @param version The version of the match to perform a play at.
     * @param moveInput The move to perform.
     * @return `String` with the error message on failure; `UserInfo` on Success.
     */
    suspend fun playMatch(
        userToken: String,
        matchId: Int,
        version: Int,
        moveInput: MoveInput,
    ): Either<String, MatchPlayedOutput> = safeRequest<MatchPlayedOutput> {
        client.post(
            urlString = "$baseUrl/match/$matchId/version/$version/play",
        ){
            contentType(ContentType.Application.Json)
            setBody(moveInput)
            header(HttpHeaders.Authorization, "Bearer $userToken")
        }
    }

    /**
     * Responsible for performing a request to the server that cancels a match.
     * @param userToken The token of the user cancelling a match.
     * @param matchId The id of the match to cancel.
     * @return `String` with the error message on failure; `MatchCancel` on Success.
     */
    suspend fun cancelSearch(userToken: String, matchId: Int): Either<String, MatchCancel> =
        safeRequest<MatchCancel> { client.post(
            urlString = "$baseUrl/match/$matchId/cancel",
        ){
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $userToken")
        }}

    /**
     * Responsible for establishing a connection with the server for Server-Sent Events.
     * This connection allows for the server to send the client updates to a match.
     * @param userToken The token of the user establishing the connection.
     * @return `Flow<MultiPlayerMatch>` a sequence of MultiPlayerMatch states.
     */
    fun connectSSE(userToken: String): Flow<MultiPlayerMatch>{
        return callbackFlow {
            val collectJob = launch{
                try {
                    client.sse(
                        host = host.address,
                        port = host.port,
                        path = "/events",
                        request = {
                            header(HttpHeaders.Authorization, "Bearer $userToken")
                        }
                    ){
                        println("ApiClientImpl: SSE Connection established in session scope for user")
                        incoming
                            .filter { it.event == EventType.MatchUpdate.name && it.data != null }
                            .mapNotNull { event ->
                                runCatching {
                                    val matchOut = clientJson.decodeFromString<MatchOutput>(event.data!!)
                                    matchOut.toMultiplayerMatch()
                                }.getOrElse {
                                    println("Bad MatchUpdate JSON: ${it.message}")
                                    null
                                }
                            }
                            .collect { match -> send(match) }
                    }
                }
                catch (e: Exception){
                    println("ApiClientImpl: SSE Connection failed or errored for user: ${e.message}")
                }
            }
            awaitClose{
                collectJob.cancel()
            }
        }.onStart { println("ApiClientImpl: SSE Flow (returned): Collector started.") }
            .onEach { match -> println("Match version: ${match.version}") }
            .onCompletion { cause -> println("ApiClientImpl: SSE Flow (returned): Collector completed. Cause: $cause") }
            .catch { cause -> println("ApiClientImpl: SSE Flow (returned): Collector caught downstream error: ${cause?.message}") }

    }

    /**
     * Responsible for performing a request to the server to get the match statistics of a user.
     * @param userToken The token of the user to get the statistics of.
     * @return `String` with the error message on failure; `List<MatchStats>` on Success.
     */
    suspend fun getMatchStatistics(userToken: String): Either<String, List<MatchStats>>
        = safeRequest<List<MatchStats>> {
            client.get("$baseUrl/user/statistics"){
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $userToken")
            }
        }

    /**
     * Responsible for cleaning up APIClient.
     * Performs a cancel of the apiScope.
     */
    override fun clear() {
        apiScope.cancel()
    }
}

/**
 * Private Auxiliary Function "safeRequest" that performs a block of code safely,
 * by surrounding it with a try/catch block.
 * @param block The block of code to run safely.
 * @return `String` for the error message on failure; `T` on success.
 */
private suspend inline fun <reified T> safeRequest(
    block: suspend () -> HttpResponse
): Either<String, T> {
    val response = try {
        block()
    } catch (e: UnresolvedAddressException) {
        return failure(e.message ?: "No internet connection")
    } catch (e: Exception) {
        return failure(e.cause?.message ?: e.message ?: "Something went wrong")
    }

    return if (response.status.isSuccess()) {
        success(response.body())
    } else {
        val error = response.body<ErrorMessage>()
        failure(error.message)
    }
}