package com.crossBoard

import com.crossBoard.domain.Admin
import com.crossBoard.domain.Email
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.UserState
import com.crossBoard.domain.Username
import com.crossBoard.domain.toMatchOutput
import com.crossBoard.domain.toMatchType
import com.crossBoard.domain.toPlayedMatch
import com.crossBoard.httpModel.*
import io.ktor.http.*
import io.ktor.server.application.Application
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import com.crossBoard.service.MatchService
import com.crossBoard.service.UsersService
import com.crossBoard.util.ApiError
import com.crossBoard.util.Failure
import com.crossBoard.util.Success
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
import io.ktor.server.application.ApplicationCall


/**
 * Responsible for configuring the server endpoints.
 * @param usersService The service for user management.
 * @param matchService The service for match management.
 */
fun Application.configureRouting(usersService: UsersService, matchService: MatchService) {
    routing {
        route("/") {
            get { call.respond("Hello World!") }
        }

        //route to get a list of users' name segment.
        route("/user/username/{username}") {
            get({
                summary = "Get users by username"
                description = "Gets the users whose username match with the given username fraction"
                tags = listOf("Users")
                request {
                    headerParameter<String>("Authorization") { // Documenting the auth header
                        description = "Bearer token"
                        required = true
                    }
                    pathParameter<String>("username") {
                        description = "The username to match with"
                    }
                }
                response {
                    HttpStatusCode.OK to { body<UserProfileOutput>() }
                    HttpStatusCode.BadRequest to { body<ErrorMessage>() }
                    HttpStatusCode.Unauthorized to { body<ErrorMessage>() }
                    HttpStatusCode.NotFound to { body<ErrorMessage>() }
                    HttpStatusCode.InternalServerError to { body<ErrorMessage>() }
                }
            }) {
                runHttp(call){
                    val username = call.parameters["username"]
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Missing username"))

                    val userToken = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                        ?: return@runHttp call.respond(HttpStatusCode.Unauthorized, ErrorMessage("Missing token"))
                    when(val userResult = usersService.getUserByToken(userToken)){
                        is Success -> {
                            val skip = call.parameters["skip"]?.toIntOrNull() ?: 0
                            val limit = call.parameters["limit"]?.toIntOrNull() ?: 10

                            call.respond(usersService.getUsersByName(username, skip, limit).map {
                                UserProfileOutput(
                                    it.id,
                                    it.username.value,
                                    it.email.value,
                                    it.token.value,
                                    it.state,
                                )
                            })
                        }
                        is Failure -> handleFailure(call, userResult.value)
                    }
                }
            }
        }

        //route to ban a user.
        route("/user/{userId}/ban"){
            post(
                {
                    summary = "Ban a user"
                    description = "Bans a user"
                    tags = listOf("Admin")
                    request {
                        headerParameter<String>("Authorization") { // Documenting the auth header
                            description = "Bearer token"
                            required = true
                        }
                        pathParameter<Int>("userId") {
                            description = "The id of the user to ban"
                        }
                    }
                    response {
                        HttpStatusCode.OK to { body<UserProfileOutput>() }
                        HttpStatusCode.BadRequest to { body<ErrorMessage>() }
                        HttpStatusCode.Unauthorized to { body<ErrorMessage>() }
                        HttpStatusCode.NotFound to { body<ErrorMessage>() }
                        HttpStatusCode.Forbidden to { body<ErrorMessage>() }
                        HttpStatusCode.InternalServerError to { body<ErrorMessage>() }
                    }
                }
            ) {
                runHttp(call){
                    val userId = call.parameters["userId"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Invalid or missing userId"))

                    val userToken = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                        ?: return@runHttp call.respond(HttpStatusCode.Unauthorized, ErrorMessage("Missing token"))

                    when(val result = usersService.getUserByToken(userToken)){
                        is Success -> {
                            val user = result.value
                            if (user.state != Admin.STATE) return@runHttp call.respond(HttpStatusCode.Forbidden, "User is not Admin")
                            when(val banResult = usersService.updateUser(userId, state = UserState.BANNED)){
                                is Success -> {
                                    val bannedUser = banResult.value
                                    call.respond(UserProfileOutput(
                                        bannedUser.id,
                                        bannedUser.username.value,
                                        bannedUser.email.value,
                                        bannedUser.token.value,
                                        bannedUser.state,
                                    ))
                                }
                                is Failure -> handleFailure(call, banResult.value)
                            }
                        }
                        is Failure -> handleFailure(call, result.value)
                    }
                }
            }
        }
        //route to unban a user.
        route("/user/{userId}/unban"){
            post(
                {
                    summary = "Unban a user"
                    description = "Unbans a user"
                    tags = listOf("Admin")
                    request {
                        headerParameter<String>("Authorization") { // Documenting the auth header
                            description = "Bearer token"
                            required = true
                        }
                        pathParameter<Int>("userId") {
                            description = "The id of the user to unban"
                        }
                    }
                    response {
                        HttpStatusCode.OK to { body<UserProfileOutput>() }
                        HttpStatusCode.BadRequest to { body<ErrorMessage>() }
                        HttpStatusCode.Unauthorized to { body<ErrorMessage>() }
                        HttpStatusCode.NotFound to { body<ErrorMessage>() }
                        HttpStatusCode.Forbidden to { body<ErrorMessage>() }
                        HttpStatusCode.InternalServerError to { body<ErrorMessage>() }
                    }
                }
            ) {
                runHttp(call){
                    val userId = call.parameters["userId"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Invalid or missing userId"))

                    val userToken = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                        ?: return@runHttp call.respond(HttpStatusCode.Unauthorized, ErrorMessage("Missing token"))

                    when(val result = usersService.getUserByToken(userToken)){
                        is Success -> {
                            val user = result.value
                            if (user.state != Admin.STATE) return@runHttp call.respond(HttpStatusCode.Forbidden, "User is not Admin")
                            when(val unbanResult = usersService.updateUser(userId, state = UserState.NORMAL)){
                                is Success -> {
                                    val unbannedUser = unbanResult.value
                                    call.respond(UserProfileOutput(
                                        unbannedUser.id,
                                        unbannedUser.username.value,
                                        unbannedUser.email.value,
                                        unbannedUser.token.value,
                                        unbannedUser.state,
                                    ))
                                }
                                is Failure -> handleFailure(call, unbanResult.value)
                            }
                        }
                        is Failure -> handleFailure(call, result.value)
                    }
                }
            }
        }
        /**
         * Route for login.
         */
        route("/user/login"){
            post({
                    summary = "Login"
                    description = "Login to the application"
                    tags = listOf("Users")
                    request {
                        body<UserLoginInput> {
                            description = "The user's credentials"
                        }
                    }
                    response {
                        HttpStatusCode.OK to { body<UserLoginOutput>() }
                        HttpStatusCode.BadRequest to { body<ErrorMessage>() }
                        HttpStatusCode.Conflict to { body<ErrorMessage>() }
                        HttpStatusCode.NotFound to { body<ErrorMessage>() }
                        HttpStatusCode.InternalServerError to { body<ErrorMessage>() }
                    }

                }) {
                runHttp(call){
                    val loginInfo = call.receive<UserLoginInput>()

                    when(val logged = usersService.login(Username(loginInfo.username.trim()),loginInfo.password)) {
                        is Success -> {
                            val user = logged.value
                            if (user.state == UserState.BANNED.name)
                                return@runHttp call.respond(HttpStatusCode.Forbidden, ErrorMessage("User is banned"))
                            call.respond(UserLoginOutput(
                                user.id,
                                user.token.value,
                                user.email.value,
                                user.state,
                            ))
                        }
                        is Failure -> handleFailure(call, logged.value)
                    }
                }
            }
        }
        //route to get the user statistics
        route("/user/statistics"){
            get({
                summary = "Gets user statistics"
                description = "Gets the user statistics for the different types of matches"
                tags = listOf("Users")
                request {
                    headerParameter<String>("Authorization") { // Documenting the auth header
                        description = "Bearer token"
                        required = true
                    }
                }
                response {
                    HttpStatusCode.OK to { body<List<MatchStats>>()}
                    HttpStatusCode.Unauthorized to {body<ErrorMessage>()}
                    HttpStatusCode.NotFound to { body<ErrorMessage>() }
                    HttpStatusCode.InternalServerError to { body<ErrorMessage>() }
                }
            }) {
                runHttp(call){
                    val userToken = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                        ?: return@runHttp call.respond(HttpStatusCode.Unauthorized, ErrorMessage("Missing token"))

                    when(val user = usersService.getUserByToken(userToken)) {
                        is Success -> {
                            call.respond(matchService.getStatistics(user.value.id))
                        }
                        is Failure -> handleFailure(call, user.value)
                    }
                }
            }
        }
        //Route to get a user.
        route("/user") {
            get({
                summary = "Get a user"
                description = "Gets a user"
                tags = listOf("Users")
                request {
                    headerParameter<String>("Authorization") { // Documenting the auth header
                        description = "Bearer token"
                        required = true
                    }
                }
                response {
                    HttpStatusCode.OK to { body<UserProfileOutput>() }
                    HttpStatusCode.Unauthorized to { body<ErrorMessage>() }
                    HttpStatusCode.NotFound to { body<ErrorMessage>() }
                    HttpStatusCode.InternalServerError to { body<ErrorMessage>() }
                }
            }) {
                runHttp(call) {
                    val userToken = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                        ?: return@runHttp call.respond(HttpStatusCode.Unauthorized, ErrorMessage("Missing token"))

                    when (val result = usersService.getUserByToken(userToken)) {
                        is Success -> {
                            val user = result.value
                            call.respond(
                                UserProfileOutput(
                                user.id,
                                user.username.value,
                                user.email.value,
                                user.token.value,
                                user.state
                            ))
                        }
                        is Failure-> handleFailure(call, result.value)
                    }
                }
            }
            //Route to update a user
            put({
                summary = "Update a user"
                description = "Update a user"
                tags = listOf("Users")
                request {
                    headerParameter<String>("Authorization") { // Documenting the auth header
                        description = "Bearer token"
                        required = true
                    }
                    body<UserUpdateInput> {
                        description = "The user's new information"
                    }
                }
                response {
                    HttpStatusCode.OK to { body<UserProfileOutput>() }
                    HttpStatusCode.BadRequest to { body<ErrorMessage>() }
                    HttpStatusCode.Unauthorized to { body<ErrorMessage>() }
                    HttpStatusCode.NotFound to { body<ErrorMessage>() }
                    HttpStatusCode.Conflict to { body<ErrorMessage>() }
                    HttpStatusCode.InternalServerError to { body<ErrorMessage>() }
                }
            }) {
                runHttp(call){
                    val userToken = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                        ?: return@runHttp call.respond(HttpStatusCode.Unauthorized, ErrorMessage("Missing token"))

                    when(val result = usersService.getUserByToken(userToken)) {
                        is Failure -> handleFailure(call, result.value)
                        is Success -> {
                            val newUserInfo = call.receive<UserUpdateInput>()
                            val userName = if (newUserInfo.username != null) Username((newUserInfo.username) as String) else null
                            val email = if (newUserInfo.email != null) Email((newUserInfo.email) as String) else null
                            when (val updatedUserResult =
                                usersService.updateUser(
                                    result.value.id,
                                    username = userName,
                                    email = email,
                                    password = newUserInfo.password,
                                )
                            ) {
                                is Success -> {
                                    val user = updatedUserResult.value
                                    call.respond(UserProfileOutput(
                                        user.id,
                                        user.username.value,
                                        user.email.value,
                                        user.token.value,
                                        user.state
                                    ))
                                }
                                is Failure -> handleFailure(call, updatedUserResult.value)
                            }
                        }
                    }
                }
            }
            //Route to create a user
            post({
                summary = "Create a user"
                description = "Create a user"
                tags = listOf("Users")
                request {
                    body<UserCreationInput> {
                        description = "The user's information"
                    }
                }
                response {
                    HttpStatusCode.Created to { body<UserCreationOutput>() }
                    HttpStatusCode.BadRequest to { body<ErrorMessage>() }
                    HttpStatusCode.Conflict to { body<ErrorMessage>() }
                    HttpStatusCode.InternalServerError to { body<ErrorMessage>() }
                }
            }) {
                runHttp(call){
                    val user = call.receive<UserCreationInput>()
                    when(
                        val result = usersService.createUser(
                            Username(user.username.trim()),
                            Email(user.email.trim()),
                            user.password
                        )
                    ){
                        is Success -> {
                            val user = result.value
                            call.respond(
                                HttpStatusCode.Created,
                                UserCreationOutput(
                                    user.id,
                                    user.token.value
                                )
                            )
                        }
                        is Failure -> handleFailure(call, result.value)
                    }
                }
            }
        }
        //route to get a user by id.
        route("user/{userId}"){
            get({
                summary = "Gets a user by id"
                description = "Gets a user by its id"
                tags = listOf("Users")
                request {
                    pathParameter<Int>("userId") {
                        description = "The id of the user"
                    }
                }
                response {
                    HttpStatusCode.OK to { body<UserProfileOutput>() }
                    HttpStatusCode.BadRequest to { body<ErrorMessage>() }
                    HttpStatusCode.NotFound to { body<ErrorMessage>() }
                    HttpStatusCode.InternalServerError to { body<ErrorMessage>() }
                }
            }) {
                runHttp(call){
                    val userId = call.parameters["userId"] ?:
                        return@runHttp call.respond(HttpStatusCode.BadRequest, "Missing user id")

                    when(val result = usersService.getUserById(userId.toInt())){
                        is Success -> {
                            val user = result.value
                            call.respond(UserProfileOutput(
                                user.id,
                                user.username.value,
                                user.email.value,
                                user.token.value,
                                user.state
                            ))
                        }
                        is Failure -> handleFailure(call, result.value)
                    }
                }
            }
        }
        //Route to join a match.
        route("/match/{match_type}"){
            post({
                summary = "Joins a match"
                description = "Joins a match of a certain type"
                tags = listOf("Matches")
                request {
                    headerParameter<String>("Authorization"){
                        description = "Bearer token"
                        required = true
                    }
                    pathParameter<String>("match_type"){
                        description = "The type of match to join"
                        required = true
                    }
                }
                response {
                    HttpStatusCode.OK to { body<MatchOutput>() }
                    HttpStatusCode.BadRequest to { body<ErrorMessage>() }
                    HttpStatusCode.Unauthorized to { body<ErrorMessage>() }
                    HttpStatusCode.NotFound to { body<ErrorMessage>() }
                    HttpStatusCode.Forbidden to { body<ErrorMessage>() }
                    HttpStatusCode.InternalServerError to { body<ErrorMessage>() }
                }
            }) {
                runHttp(call){
                    val userToken = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                        ?: return@runHttp call.respond(HttpStatusCode.Unauthorized, ErrorMessage("Missing Token"))

                    when(val user = usersService.getUserByToken(userToken)) {
                        is Failure -> handleFailure(call, user.value)
                        is Success -> {
                            if (user.value.state == UserState.BANNED.name)
                                return@runHttp call.respond(HttpStatusCode.Forbidden, ErrorMessage("User is banned"))
                            val matchTypeInput = call.parameters["match_type"]
                                ?: return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Missing game type"))

                            val matchType = matchTypeInput.toMatchType()
                            when(val createdMatch = matchService.enterMatch(user.value.id, matchType)){
                                is Success -> call.respond(createdMatch.value.toMatchOutput())
                                is Failure -> handleFailure(call, createdMatch.value)
                            }
                        }
                    }

                }
            }
        }
        //Route to forfeit a match.
        route("/match/{matchId}/forfeit"){
            post({
                summary = "Forfeit a match"
                description = "Forfeit a match"
                tags = listOf("Matches")
                request {
                    headerParameter<String>("Authorization"){
                        description = "Bearer token"
                        required = true
                    }
                    pathParameter<Int>("matchId"){
                        description = "The id of the match to forfeit"
                        required = true
                    }
                }
                response {
                    HttpStatusCode.OK to { body<MatchOutput>() }
                    HttpStatusCode.BadRequest to { body<ErrorMessage>() }
                    HttpStatusCode.Unauthorized to { body<ErrorMessage>() }
                    HttpStatusCode.NotFound to { body<ErrorMessage>() }
                    HttpStatusCode.InternalServerError to { body<ErrorMessage>() }
                }
            }) {
                runHttp(call){
                    val matchId = call.parameters["matchId"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Invalid or missing matchId"))

                    val userToken = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                        ?: return@runHttp call.respond(HttpStatusCode.Unauthorized, ErrorMessage("Missing token"))

                    when(val user = usersService.getUserByToken(userToken)) {
                        is Failure -> handleFailure(call, user.value)
                        is Success -> {
                            when(val forfeitedMatch = matchService.forfeit(matchId, user.value.id)){
                                is Success -> call.respond(forfeitedMatch.value.toMatchOutput())
                                is Failure -> handleFailure(call, forfeitedMatch.value)
                            }
                        }
                    }

                }
            }
        }
        //Route to get a match by its id.
        route("/match/{matchId}"){
            get({
                summary = "Gets a match by id"
                description = "Gets a match by its id"
                tags = listOf("Matches")
                request {
                    pathParameter<Int>("matchId"){
                        description = "The id of the match"
                        required = true
                    }
                }
                response {
                    HttpStatusCode.OK to { body<MatchOutput>() }
                    HttpStatusCode.BadRequest to { body<ErrorMessage>() }
                    HttpStatusCode.NotFound to { body<ErrorMessage>() }
                    HttpStatusCode.InternalServerError to { body<ErrorMessage>() }
                }
            }) {
                runHttp(call){
                    val matchId = call.parameters["matchId"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Invalid or missing matchId"))

                    when(val match = matchService.getMatchById(matchId)){
                        is Success -> call.respond(match.value.toMatchOutput())
                        is Failure -> handleFailure(call, match.value)
                    }
                }
            }
        }
        //Route to play a match.
        route("/match/{matchId}/version/{version}/play"){
            post({
                summary = "Play a match"
                description = "Play a match"
                tags = listOf("Matches")
                request {
                    headerParameter<String>("Authorization"){
                        description = "Bearer token"
                        required = true
                    }
                    pathParameter<Int>("matchId"){
                        description = "The id of the match to play"
                        required = true
                    }
                }
                response {
                    HttpStatusCode.OK to { body<MatchOutput>() }
                    HttpStatusCode.BadRequest to { body<ErrorMessage>() }
                    HttpStatusCode.Unauthorized to { body<ErrorMessage>() }
                    HttpStatusCode.NotFound to { body<ErrorMessage>() }
                    HttpStatusCode.Forbidden to { body<ErrorMessage>() }
                    HttpStatusCode.InternalServerError to { body<ErrorMessage>() }
                    HttpStatusCode.Conflict to { body<ErrorMessage>() }
                }
            }) {
                runHttp(call){
                    val matchId = call.parameters["matchId"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Invalid or missing matchId"))
                    val userToken = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                        ?: return@runHttp call.respond(HttpStatusCode.Unauthorized, ErrorMessage("Missing token"))

                    val version = call.parameters["version"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Invalid or missing version"))

                    when(val match = matchService.getMatchById(matchId)){
                        is Failure -> handleFailure(call, match.value)
                        is Success -> {
                            if (match.value.version != version) return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Version Mismatch"))
                        }
                    }
                    when(val user = usersService.getUserByToken(userToken)) {
                        is Failure -> handleFailure(call, user.value)
                        is Success -> {
                            when(val match = matchService.getMatchById(matchId)) {
                                is Failure -> handleFailure(call, match.value)
                                is Success -> {
                                    val matchType = match.value.matchType
                                    val moveInput = receiveMoveInput(call, matchType)
                                    val move = moveInput.toMove()

                                    when(val updatedMatch = matchService.playMatch(matchId, user.value.id, move, version)){
                                        is Success -> call.respond(updatedMatch.value.toPlayedMatch())
                                        is Failure -> handleFailure(call, updatedMatch.value)
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
        //route to get a match by its version.
        route("/match/{matchId}/version/{version}"){
            get({
                summary = "Gets a match by its version"
                description = "Gets a match by its version"
                tags = listOf("Matches")
                request {
                    pathParameter<Int>("matchId"){
                        description = "The id of the match"
                        required = true
                    }
                    pathParameter<Int>("version"){
                        description = "The version of the match"
                        required = true
                    }
                }
                response {
                    HttpStatusCode.OK to { body<MatchOutput>() }
                    HttpStatusCode.BadRequest to { body<ErrorMessage>() }
                    HttpStatusCode.NotFound to { body<ErrorMessage>() }
                    HttpStatusCode.InternalServerError to { body<ErrorMessage>() }
                    HttpStatusCode.Conflict to { body<ErrorMessage>() }
                }
            }) {
                runHttp(call){
                    val matchId = call.parameters["matchId"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Invalid or missing matchId"))

                    val version = call.parameters["version"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Invalid or missing version"))

                    when(val match = matchService.getMatchByVersion(matchId, version)){
                        is Success -> call.respond(HttpStatusCode.OK, match.value.toMatchOutput())
                        is Failure -> handleFailure(call, match.value)
                    }
                }
            }
        }
        //route to cancel a match.
        route("/match/{matchId}/cancel"){
            post({
                summary = "Cancel a match"
                description = "Cancel a match"
                tags = listOf("Matches")
                request {
                    headerParameter<String>("Authorization"){
                        description = "Bearer token"
                        required = true
                    }
                    pathParameter<Int>("matchId"){
                        description = "The id of the match to cancel"
                        required = true
                    }
                }
                response {
                    HttpStatusCode.OK to { body<MatchOutput>() }
                    HttpStatusCode.BadRequest to { body<ErrorMessage>() }
                    HttpStatusCode.Unauthorized to { body<ErrorMessage>() }
                    HttpStatusCode.NotFound to { body<ErrorMessage>() }
                    HttpStatusCode.Conflict to { body<ErrorMessage>() }
                }
            }) {
                runHttp(call){
                    val userToken = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                        ?: return@runHttp call.respond(HttpStatusCode.Unauthorized, ErrorMessage("Missing token"))

                    val matchId = call.parameters["matchId"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Invalid or missing matchId"))

                    when(val user = usersService.getUserByToken(userToken)){
                        is Success -> {
                            when(val match = matchService.cancelSearch(user.value.id, matchId)){
                                is Success -> {
                                    call.respond(match.value)
                                }
                                is Failure -> handleFailure(call, match.value)

                            }
                        }
                        is Failure -> handleFailure(call, user.value)
                    }

                }
            }
        }
    }
}

/**
 * Responsible for managing the responses given a type of error.
 * @param call The call to respond to.
 * @param error The type of error to handle.
 */
suspend fun handleFailure(call: ApplicationCall, error: ApiError) {
    when (error) {
        ApiError.USER_NOT_FOUND -> call.respond(HttpStatusCode.NotFound, ErrorMessage("User not found"))
        ApiError.USERNAME_ALREADY_EXISTS -> call.respond(HttpStatusCode.Conflict, ErrorMessage("Username already exists"))
        ApiError.EMAIL_ALREADY_EXISTS  -> call.respond(HttpStatusCode.Conflict, ErrorMessage("Email already exists"))
        ApiError.UNAUTHORIZED -> call.respond(HttpStatusCode.Unauthorized, ErrorMessage("Unauthorized action"))
        ApiError.MATCH_NOT_FOUND -> call.respond(HttpStatusCode.NotFound, ErrorMessage("Match not found"))
        ApiError.USER_ALREADY_IN_MATCH -> call.respond(HttpStatusCode.Conflict, ErrorMessage("User already in an ongoing match"))
        ApiError.USER_NOT_IN_THIS_MATCH -> call.respond(HttpStatusCode.Unauthorized, ErrorMessage("User does not belong in this match"))
        ApiError.INCORRECT_PLAYER_TYPE_FOR_THIS_USER -> call.respond(HttpStatusCode.BadRequest, ErrorMessage("This user is not the specified player type in the match"))
        ApiError.VERSION_MISMATCH -> call.respond(HttpStatusCode.Conflict, ErrorMessage("Version mismatch"))
        ApiError.WRONG_PASSWORD -> call.respond(HttpStatusCode.Conflict, ErrorMessage("Wrong password"))
        ApiError.MATCH_NOT_IN_WAITING_STATE -> call.respond(HttpStatusCode.Conflict, ErrorMessage("Match not in waiting state"))
        else -> call.respond(HttpStatusCode.InternalServerError, ErrorMessage("Unexpected error"))
    }
}

/**
 * Auxiliary function for routing.
 * @param call The routing call to respond to.
 * @param block The block to run safely.
 */
private suspend fun runHttp(call: RoutingCall, block: suspend () -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        call.respond(HttpStatusCode.InternalServerError, ErrorMessage(e.cause?.message ?: e.message ?: "Unknown error"))
    }
}

/**
 * Responsible for the deserialization of the move input.
 * @param call The Routing call where that contains the data.
 * @param matchType The match type that determines which type of move input to use.
 */
private suspend fun receiveMoveInput(call: RoutingCall, matchType: MatchType): MoveInput = when(matchType){
    MatchType.TicTacToe -> call.receive<TicTacToeMoveInput>()
    MatchType.Reversi -> call.receive<ReversiMoveInput>()
}
