import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.postgresql.ds.PGSimpleDataSource
import repository.jdbc.JdbcMatchRepo
import repository.jdbc.JdbcUserRepo
import service.MatchService
import service.UsersService

const val URL = "URL_PS"



fun main() {
    val port = 8000
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val url = System.getenv(URL)
    val jdbc = PGSimpleDataSource().apply {
        setURL(url)
        user = "postgres"
        password = "12345"
    }

    val userService = UsersService(JdbcUserRepo(jdbc))
    val matchService = MatchService(JdbcMatchRepo(jdbc))

    configureCors()
    configureSerialization()
    configureRouting(userService, matchService)
}


