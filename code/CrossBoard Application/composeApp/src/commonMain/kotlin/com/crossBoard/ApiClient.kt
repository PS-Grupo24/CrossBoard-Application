package com.crossBoard

import com.crossBoard.domain.Email
import com.crossBoard.domain.Token
import com.crossBoard.domain.UserInfo
import com.crossBoard.domain.Username
import com.crossBoard.httpModel.ErrorMessage
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
import com.crossBoard.interfaces.Clearable
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.network.*
import kotlinx.serialization.SerializationException
import com.crossBoard.util.Either
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ApiClient(
    private val client: HttpClient,
    host: Host,
    private val apiScope: CoroutineScope = CoroutineScope(SupervisorJob())
): Clearable {
    private val baseUrl = host.hostname

    private var gameWebSocketSession: DefaultWebSocketSession? = null
    private val wsMutex = Mutex()

    private val _incomingMessages = MutableSharedFlow<Frame>(
        replay = 0,
        extraBufferCapacity = 64
    )
    val incomingMessages: SharedFlow<Frame> = _incomingMessages.asSharedFlow()

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

    suspend fun forfeitMatch(userToken: String, matchId: Int): Either<String, MatchOutput> {
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

    suspend fun getMatch(matchId: Int): Either<String, MatchOutput> {
        val response = try {
            client.get(
                urlString = "$baseUrl/match/$matchId"
            )
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

    suspend fun playMatch(
        userToken: String,
        matchId: Int,
        version: Int,
        moveInput: MoveInput,
    ): Either<String, MatchPlayedOutput> {
        val response = try {
            client.post(
                urlString = "$baseUrl/match/$matchId/version/$version/play",
            ){
                contentType(ContentType.Application.Json)
                setBody(moveInput)
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
            val move = response.body<MatchPlayedOutput>()
            Either.Right(move)
        }
        else {
            val error = response.body<ErrorMessage>()
            Either.Left(error.message)
        }
    }

    suspend fun cancelSearch(userToken: String, matchId: Int): Either<String, MatchCancel> {
        val response = try {
            client.post(
                urlString = "$baseUrl/match/$matchId/cancel",
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
            val move = response.body<MatchCancel>()
            Either.Right(move)
        }
        else {
            val error = response.body<ErrorMessage>()
            Either.Left(error.message)
        }

    }

    fun connectGameWebSocket(userId:Int, userToken: String): Flow<Frame>{
        if (gameWebSocketSession?.isActive == true){
            return incomingMessages
        }

        apiScope.launch {
            try {
                client.webSocket(
                    method = HttpMethod.Get,
                    host = baseUrl,
                    path = "/match-ws",
                    request = { header(HttpHeaders.Authorization, "Bearer $userToken") },
                ){
                    wsMutex.withLock { gameWebSocketSession = this}

                    for (frame in incoming) {
                        _incomingMessages.emit(frame)
                    }
                }
            }
            catch (e: Exception) {
                //_connectionStatusFlow.value = ConnectionStatus.Error(e.message)
            }
        }

        return incomingMessages
            .onStart { println("WS Flow: Collector started incoming messages flow for user")}
            .onEach { frame -> println("WS Flow: Emitted frame from flow: ${frame::class.simpleName}") }
            .onCompletion { cause -> println("WS Flow: Collector completed on incoming messages flow for user $userId. Cause: $cause") }
            .catch { cause -> println("WS Flow: Collector caught exception downstream: ${cause.message}") }
    }

    suspend fun sendGameWebSocketMessage(frame: Frame) {
        wsMutex.withLock {
            val session = gameWebSocketSession
            if (session?.isActive == true) {
                try {
                    session.send(frame)
                    println("WS Send: Sent frame: ${frame::class.simpleName}")
                }
                catch (e: Exception){
                    println("WS Send: Failed to send frame: ${e.message}")
                    throw e
                }
            }
        }
    }

    suspend fun disconnectGameWebSocket(){
        wsMutex.withLock {
            val session = gameWebSocketSession

            if (session?.isActive == true) {
                try {
                    session.close(CloseReason(CloseReason.Codes.NORMAL, "Client disconnected"))
                    println("WS Disconnect: Sent close frame.")
                }
                catch (e: Exception){
                    println("WS Disconnect: Failed to send close frame: ${e.message}")
                }
                finally {
                    gameWebSocketSession = null
                }
            } else {
                println("WS Disconnect: No active session to disconnect.")
            }
        }
    }

    suspend fun getMatchStatistics(userToken: String): Either<String, List<MatchStats>>{
        val response = try {
            client.get("$baseUrl/user/statistics"){
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $userToken")
            }
        }
        catch (e: UnresolvedAddressException) {
            return Either.Left(e.message ?: "No internet connection")
        }
        catch (e: Exception) {
            return Either.Left(e.message ?: "Something went wrong")
        }

        return if (response.status.value in 200 .. 299){
            Either.Right(response.body<List<MatchStats>>())
        }
        else {
            val error = response.body<ErrorMessage>()
            Either.Left(error.message)
        }
    }
    override fun clear() {
        apiScope.cancel()
    }
}