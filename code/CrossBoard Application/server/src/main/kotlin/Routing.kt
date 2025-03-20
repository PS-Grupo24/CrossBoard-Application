import httpModel.UserCreation
import httpModel.UserUpdate
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
                    val userId = call.parameters["userId"]?.toUIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, "Invalid or missing userId")

                    when (val user = usersService.getUserById(userId)) {
                        is Success -> call.respond(user.value)
                        is Failure -> handleFailure(call, user.value)
                    }
                }
            }
            put {
                runHttp(call){
                    val userId = call.parameters["userId"]?.toUIntOrNull()
                        ?: return@runHttp call.respond(HttpStatusCode.BadRequest, "Invalid or missing userId")

                    val newUserInfo = call.receive<UserUpdate>()
                    when (val updatedUser =
                        usersService.updateUser(
                            userId,
                            newUserInfo.username,
                            newUserInfo.email,
                            newUserInfo.password
                        )
                    ) {
                        is Success -> call.respond(updatedUser.value)
                        is Failure -> handleFailure(call, updatedUser.value)
                    }
                }
            }
        }
        route("/user"){
            post {
                runHttp(call){
                    val user = call.receive<UserCreation>()

                    when(val createdUser = usersService.createUser(user.username, user.email, user.password)){
                        is Success -> call.respond(createdUser.value)
                        is Failure -> handleFailure(call, createdUser.value)
                    }
                }
            }
        }
    }
}

private suspend fun handleFailure(call: RoutingCall, error: API_Error) {
    when (error) {
        API_Error.USER_NOT_FOUND -> call.respond(HttpStatusCode.NotFound, "User not found")
        API_Error.USERNAME_ALREADY_EXISTS -> call.respond(HttpStatusCode.Conflict, "Username already exists")
        API_Error.EMAIL_ALREADY_EXISTS  -> call.respond(HttpStatusCode.Conflict, "Email already exists")
        API_Error.UNAUTHORIZED -> call.respond(HttpStatusCode.Unauthorized, "Unauthorized action")
        else -> call.respond(HttpStatusCode.InternalServerError, "Unexpected error")
    }
}

suspend fun runHttp(call: RoutingCall, block: suspend () -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        call.respond(HttpStatusCode.InternalServerError, e.cause?.message ?: "Unknown error")
    }
}
