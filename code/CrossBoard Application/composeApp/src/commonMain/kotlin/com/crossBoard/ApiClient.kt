package com.crossBoard

import com.crossBoard.domain.Email
import com.crossBoard.domain.MultiPlayerMatch
import com.crossBoard.domain.Token
import com.crossBoard.domain.UserInfo
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
import com.crossBoard.utils.clientJson
import io.ktor.client.plugins.sse.sse
import io.ktor.client.plugins.websocket.*
import io.ktor.client.statement.HttpResponse
import io.ktor.sse.ServerSentEvent
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * class ApiClient, responsible for the requests to the server
 */
class ApiClient(
    private val client: HttpClient,
    val host: Host,
    private val apiScope: CoroutineScope = CoroutineScope(SupervisorJob())
): Clearable {
    private val baseUrl = "http://${host.host}:${host.port}"

    private var gameWebSocketSession: DefaultWebSocketSession? = null
    private val wsMutex = Mutex()

    private val _incomingMessages = MutableSharedFlow<Frame>(
        replay = 1,
        extraBufferCapacity = 64
    )
    val incomingMessages: SharedFlow<Frame> = _incomingMessages.asSharedFlow()

    /**
     * Function "banUser", responsible for the request to ban a user.
     * @param userToken The token of the admin performing the ban.
     * @param userId The id of the user to forfeit.
     * @return the error message in String if failure or UserInfo if success
     */
    suspend fun banUser(userToken: String, userId: Int): Either<String, UserInfo> {
        val response = try {
            client.post("$baseUrl/user/$userId/ban") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $userToken")
            }
        }
        catch (e: UnresolvedAddressException) {
            return Either.Left(e.message ?: "No internet connection")
        }
        catch (e: Exception){
            return Either.Left(e.message ?: "Something went wrong")
        }
        return if (response.status.value in 200 .. 299){
            val body = response.body<UserProfileOutput>()
            Either.Right(UserInfo(body.id, Token(body.token), Username(body.username), Email(body.email), body.state))

        }
        else {
            val error = response.body<ErrorMessage>()
            Either.Left(error.message)
        }
    }

    /**
     * Function "unbanUser" responsible for unbanning a user.
     * @param userToken The token of the admin performing unban.
     * @param userId The id of the user to unban.
     * @return the error message in String if failure or UserInfo if success
     */
    suspend fun unbanUser(userToken: String, userId: Int): Either<String, UserInfo> {
        val response = try {
            client.post("$baseUrl/user/$userId/unban") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $userToken")
            }
        }
        catch (e: UnresolvedAddressException) {
            return Either.Left(e.message ?: "No internet connection")
        }
        catch (e: Exception){
            return Either.Left(e.message ?: "Something went wrong")
        }
        return if (response.status.value in 200 .. 299){
            val body = response.body<UserProfileOutput>()
            Either.Right(UserInfo(body.id, Token(body.token), Username(body.username), Email(body.email), body.state))
        }
        else {
            val error = response.body<ErrorMessage>()
            Either.Left(error.message)
        }
    }

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
            return Either.Left(e.message ?: "No internet connection")
        }
        catch (e: Exception){
            return Either.Left(e.cause?.message ?: e.message ?: "Something went wrong")
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
            Either.Right(users)
        }
        else {
            val error = response.body<ErrorMessage>()
            Either.Left(error.message)
        }
    }

    suspend fun login(username: String, password: String): Either<String, UserLoginOutput> {
        val response = try {
            println(baseUrl)
            client.post("$baseUrl/user/login") {
                contentType(ContentType.Application.Json)
                setBody(UserLoginInput(username, password))
            }
        }
        catch (e: UnresolvedAddressException) {
            return Either.Left(e.message ?: "No internet connection")
        }
        catch (e: Exception){
            return Either.Left(e.cause?.message ?: e.message ?: "Something went wrong")
        }

        return if (response.status.value in 200 .. 299){
            val logged = response.body<UserLoginOutput>()
            Either.Right(logged)
        }
        else {
            val error = response.body<ErrorMessage>()
            Either.Left(error.message)
        }
    }

    suspend fun register(username: String, email: String, password: String): Either<String, UserCreationOutput> {
        val response = try {
            client.post("$baseUrl/user") {
                contentType(ContentType.Application.Json)
                setBody(UserCreationInput(username, email, password))
            }
        }
        catch (e: UnresolvedAddressException) {
            return Either.Left(e.message ?: "No internet connection")
        }
        catch (e: Exception){
            return Either.Left(e.cause?.message ?: e.message ?: "Something went wrong")
        }

        return if (response.status.value in 200 .. 299){
            val logged = response.body<UserCreationOutput>()
            Either.Right(logged)
        }
        else {
            val error = response.body<ErrorMessage>()
            Either.Left(error.message)
        }
    }

    suspend fun getUserById(userId:Int): Either<String, UserProfileOutput> {
        val response = try {
            client.get("$baseUrl/user/$userId"){
                contentType(ContentType.Application.Json)
            }
        }
        catch (e: UnresolvedAddressException){
            return Either.Left(e.message ?: "No internet connection")
        }
        catch (e: Exception){
            return Either.Left(e.message ?: "Something went wrong")
        }

        return if (response.status.value in 200 .. 299){
            val user = response.body<UserProfileOutput>()
            Either.Right(user)
        }
        else {
            val error = response.body<ErrorMessage>()
            Either.Left(error.message)
        }
    }

    suspend fun getMatchByVersion(matchId: Int, version: Int): Either<String, MatchOutput> {
        val response = try {
            client.get("$baseUrl$matchId/$version"){
                contentType(ContentType.Application.Json)
            }
        }
        catch (e: UnresolvedAddressException) {
            return Either.Left(e.message ?: "No internet connection")
        }
        catch (e: Exception) {
            return Either.Left(e.cause?.message ?: e.message ?: "Something went wrong")
        }

        return if (response.status.value in 200 .. 299){
            val match = response.body<MatchOutput>()
            Either.Right(match)
        }
        else {
            val error = response.body<ErrorMessage>()
            Either.Left(error.message)
        }
    }

    suspend fun enterMatch(userToken: String, gameType: String): Either<String, MatchOutput> {
        val response = try {
            client.post(
                urlString = "$baseUrl/match/$gameType",
            ){
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $userToken")
            }
        }
        catch (e: SerializationException) {
            return Either.Left(e.message ?: "Serialization exception")
        }
        catch (e: UnresolvedAddressException) {
            return Either.Left(e.message ?: "No internet connection")
        }

        return if (response.status.value in 200 .. 299){
            val match = response.body<MatchOutput>()
            Either.Right(match)
        }
        else {
            val error = response.body<ErrorMessage>()
            Either.Left(error.message)
        }
    }

    suspend fun forfeitMatch(userToken: String, matchId: Int): Either<String, MatchOutput>
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
            return Either.Left(e.message ?: "Serialization exception")
        }
        catch (e: UnresolvedAddressException) {
            return Either.Left(e.message ?: "No internet connection")
        }

        return if (response.status.value in 200 .. 299){
            val forfeitedMatch = response.body<MatchOutput>()
            Either.Right(forfeitedMatch)
        }
        else {
            val error = response.body<ErrorMessage>()
            Either.Left(error.message)
        }
    }

    suspend fun getMatch(matchId: Int): Either<String, MatchOutput> =
        safeRequest<MatchOutput> {
            client.get(
                urlString = "$baseUrl/match/$matchId"
            )
        }

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

    suspend fun cancelSearch(userToken: String, matchId: Int): Either<String, MatchCancel> =
        safeRequest<MatchCancel> { client.post(
            urlString = "$baseUrl/match/$matchId/cancel",
        ){
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $userToken")
        }}

    fun connectSSE(userToken: String): Flow<MultiPlayerMatch>{
        return callbackFlow {
            val collectJob = launch{
                try {
                    client.sse(
                        host = host.host,
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

    suspend fun getMatchStatistics(userToken: String): Either<String, List<MatchStats>>
        = safeRequest<List<MatchStats>> {
            client.get("$baseUrl/user/statistics"){
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $userToken")
            }
        }

    override fun clear() {
        apiScope.cancel()
    }
}

private suspend inline fun <reified T> safeRequest(
    block: suspend () -> HttpResponse
): Either<String, T> {
    val response = try {
        block()
    } catch (e: UnresolvedAddressException) {
        return Either.Left(e.message ?: "No internet connection")
    } catch (e: Exception) {
        return Either.Left(e.cause?.message ?: e.message ?: "Something went wrong")
    }

    return if (response.status.isSuccess()) {
        Either.Right(response.body())
    } else {
        val error = response.body<ErrorMessage>()
        Either.Left(error.message)
    }
}