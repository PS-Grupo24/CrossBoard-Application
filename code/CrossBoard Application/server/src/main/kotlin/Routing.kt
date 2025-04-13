import domain.*
import httpModel.*
import io.ktor.http.*
import io.ktor.server.application.Application
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.coroutines.delay
import service.MatchService
import service.UsersService
import util.*

fun Application.configureRouting(usersService: UsersService, matchService: MatchService) {
    routing {
        route("/") {
            get { call.respond("Hello World!") }
        }
        route("/user") {
            get {
                runHttp(call) {
                    val userToken = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                        ?: return@runHttp call.respond(HttpStatusCode.Unauthorized, ErrorMessage("Missing token"))

                    when (val user = usersService.getUserByToken(userToken)) {
                        is Success -> call.respond(user.value)
                        is Failure -> handleFailure(call, user.value)
                    }
                }
            }
            //Route to update a user
            put {
                runHttp(call){
                    val userToken = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                        ?: return@runHttp call.respond(HttpStatusCode.Unauthorized, ErrorMessage("Missing token"))

                    when(val user = usersService.getUserByToken(userToken)) {
                        is Failure -> handleFailure(call, user.value)
                        is Success -> {
                            val newUserInfo = call.receive<UserUpdateInput>()
                            val userName = if (newUserInfo.username != null) Username((newUserInfo.username) as String) else null
                            val email = if (newUserInfo.email != null) Email((newUserInfo.email) as String) else null
                            val password = if (newUserInfo.password != null) Password((newUserInfo.password) as String) else null
                            when (val updatedUser =
                                usersService.updateUser(
                                    user.value.id,
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
        route("/match/{gametype}"){
            post {
                runHttp(call){

                    val userToken = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                        ?: return@runHttp call.respond(HttpStatusCode.Unauthorized, ErrorMessage("Missing token"))
                    when(val user = usersService.getUserByToken(userToken)) {
                        is Failure -> handleFailure(call, user.value)
                        is Success -> {
                            val gametypeInput = call.parameters["gametype"]
                                ?: return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Missing game type"))

                            val gameType = gametypeInput.toGameType()
                                ?: return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Unknown GameType"))
                            when(val createdMatch = matchService.enterMatch(user.value.id, gameType)){
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
            post {
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
            get {
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
            post {
                runHttp(call){
                    val matchId = call.parameters["matchId"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Invalid or missing matchId"))

                    val userToken = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                        ?: return@runHttp call.respond(HttpStatusCode.Unauthorized, ErrorMessage("Missing token"))

                    val version = call.parameters["version"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Invalid or missing version"))

                    val match = matchService.getMatchById(matchId)
                    if(match is Failure) return@runHttp call.respond(HttpStatusCode.NotFound, ErrorMessage("Match not found"))

                    when(val user = usersService.getUserByToken(userToken)) {
                        is Failure -> handleFailure(call, user.value)
                        is Success -> {
                            when(val match = matchService.getMatchById(matchId)) {
                                is Failure -> handleFailure(call, match.value)
                                is Success -> {
                                    val gametype = match.value.gameType
                                    val moveInput = call.receive<MoveInput>()
                                    //val moveInput = parseMoveInput(body, gametype) ?: return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Invalid move input"))

                                    val move = moveInput.toMove(gametype) ?: return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Invalid move input"))

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
            get {
                runHttp(call){
                    val matchId = call.parameters["matchId"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Invalid or missing matchId"))

                    val version = call.parameters["version"]?.toIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, ErrorMessage("Invalid or missing version"))


                }
            }
        }
    }
}

private suspend fun handleFailure(call: RoutingCall, error: ApiError) {
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
        else -> call.respond(HttpStatusCode.InternalServerError, ErrorMessage("Unexpected error"))
    }
}

suspend fun runHttp(call: RoutingCall, block: suspend () -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        call.respond(HttpStatusCode.InternalServerError, ErrorMessage(e.cause?.message ?: e.message ?: "Unknown error"))
    }
}
