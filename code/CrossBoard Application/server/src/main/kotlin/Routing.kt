import domain.Email
import domain.Password
import domain.Username
import domain.toGameType
import httpModel.MatchCreationInput
import httpModel.MoveInput
import httpModel.UserCreationInput
import httpModel.UserUpdateInput
import httpModel.toMatchOutput
import httpModel.toMove
import httpModel.toMoveOutput
import io.ktor.http.*
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import service.MatchService
import service.UsersService
import util.*


fun Application.configureRouting(usersService: UsersService, matchService: MatchService) {
    routing {
        route("/") {
            get { call.respond("Hello World!") }
        }
        route("/user/{userId}") {
            get {
                runHttp(call) {
                    val userId = call.parameters["userId"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, "Invalid or missing userId")

                    when (val user = usersService.getUserById(userId)) {
                        is Success -> call.respond(user.value)
                        is Failure -> handleFailure(call, user.value)
                    }
                }
            }
            //Route to update a user
            put {
                runHttp(call){
                    val userId = call.parameters["userId"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, "Invalid or missing userId")

                    val newUserInfo = call.receive<UserUpdateInput>()
                    val userName = if (newUserInfo.username != null) Username((newUserInfo.username) as String) else null
                    val email = if (newUserInfo.email != null) Email((newUserInfo.email) as String) else null
                    val password = if (newUserInfo.password != null) Password((newUserInfo.password) as String) else null
                    when (val updatedUser =
                        usersService.updateUser(
                            userId,
                            userName,
                            email,
                            password
                        )
                    ) {
                        is Success -> call.respond(updatedUser.value)
                        is Failure -> handleFailure(call, updatedUser.value)
                    }
                }
            }
        }
        //Route to create a user
        route("/user"){
            post {
                runHttp(call){
                    val user = call.receive<UserCreationInput>()
                    when(
                        val createdUser = usersService.createUser(
                        Username(user.username),
                        Email(user.email),
                        Password(user.password))
                    ){
                        is Success -> call.respond(createdUser.value)
                        is Failure -> handleFailure(call, createdUser.value)
                    }
                }
            }
        }
        //Route to join a match.
        route("/match/user/{userId}"){
            post {
                runHttp(call){
                    val userId = call.parameters["userId"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, "Invalid or missing userId")

                    val matchCreationInfo = call.receive<MatchCreationInput>()
                    val gameType = matchCreationInfo.gameType.toGameType() ?: return@runHttp call.respond(HttpStatusCode.BadRequest, "Unknown GameType")
                    when(val createdMatch = matchService.enterMatch(userId, gameType)){
                        is Success -> call.respond(createdMatch.value.toMatchOutput())
                        is Failure -> handleFailure(call, createdMatch.value)
                    }
                }
            }
        }
        //Route to forfeit a match.
        route("/match/{matchId}/forfeit/{userId}"){
            put {
                runHttp(call){
                    val matchId = call.parameters["matchId"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, "Invalid or missing matchId")

                    val userId = call.parameters["userId"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, "Invalid or missing userId")

                    when(val forfeitedMatch = matchService.forfeit(matchId, userId)){
                        is Success -> call.respond(forfeitedMatch.value.toMatchOutput())
                        is Failure -> handleFailure(call, forfeitedMatch.value)
                    }
                }
            }
        }
        //Route to get a match by its id.
        route("/match/{matchId}"){
            get {
                runHttp(call){
                    val matchId = call.parameters["matchId"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, "Invalid or missing matchId")

                    when(val match = matchService.getMatchById(matchId)){
                        is Success -> call.respond(match.value.toMatchOutput())
                        is Failure -> handleFailure(call, match.value)
                    }
                }
            }
        }
        //Route to play a match.
        route("/match/{matchId}/play/{userId}"){
            put {
                runHttp(call){
                    val matchId = call.parameters["matchId"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, "Invalid or missing matchId")

                    val userId = call.parameters["userId"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, "Invalid or missing userId")

                    val move = call.receive<MoveInput>().toMove() ?:
                        return@runHttp call.respond(HttpStatusCode.BadRequest, "Error converting moveInput to Move")
                    when(val updatedMatch = matchService.playMatch(matchId, userId, move)){
                        is Success -> call.respond(updatedMatch.value.toMoveOutput())
                        is Failure -> handleFailure(call, updatedMatch.value)
                    }
                }
            }
        }
    }
}

private suspend fun handleFailure(call: RoutingCall, error: ApiError) {
    when (error) {
        ApiError.USER_NOT_FOUND -> call.respond(HttpStatusCode.NotFound, "User not found")
        ApiError.USERNAME_ALREADY_EXISTS -> call.respond(HttpStatusCode.Conflict, "Username already exists")
        ApiError.EMAIL_ALREADY_EXISTS  -> call.respond(HttpStatusCode.Conflict, "Email already exists")
        ApiError.UNAUTHORIZED -> call.respond(HttpStatusCode.Unauthorized, "Unauthorized action")
        ApiError.MATCH_NOT_FOUND -> call.respond(HttpStatusCode.NotFound, "Match not found")
        ApiError.USER_ALREADY_IN_MATCH -> call.respond(HttpStatusCode.Conflict, "User already in an ongoing match")
        ApiError.USER_NOT_IN_THIS_MATCH -> call.respond(HttpStatusCode.Unauthorized, "User does not belong in this match")
        ApiError.INCORRECT_PLAYER_TYPE_FOR_THIS_USER -> call.respond(HttpStatusCode.BadRequest, "This user is not the specified player type in the match")
        else -> call.respond(HttpStatusCode.InternalServerError, "Unexpected error")
    }
}

suspend fun runHttp(call: RoutingCall, block: suspend () -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        call.respond(HttpStatusCode.InternalServerError, e.cause?.message ?: e.message ?: "Unknown error")
    }
}
