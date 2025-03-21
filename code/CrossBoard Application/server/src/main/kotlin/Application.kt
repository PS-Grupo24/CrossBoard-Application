import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.example.project.SERVER_PORT
import repository.memoryRepositories.MemoryMatchRep
import repository.memoryRepositories.MemoryUserRep
import service.MatchService
import service.UsersService

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val userRep = MemoryUserRep()
    val userService = UsersService(userRep)
    val matchService = MatchService(MemoryMatchRep)
    configureSerialization()
    configureRouting(userService, matchService)
}


