package com.crossBoard

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.postgresql.ds.PGSimpleDataSource
import com.crossBoard.repository.jdbc.JdbcMatchRepo
import com.crossBoard.repository.jdbc.JdbcUserRepo
import com.crossBoard.repository.memoryRepositories.MemoryMatchRep
import com.crossBoard.service.MatchService
import com.crossBoard.service.UsersService

const val URL = "URL_PS"
const val PORT = "PS_PORT"

fun main() {
    val port = System.getenv(PORT)?.toInt() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val url = System.getenv(URL)
    val jdbc = PGSimpleDataSource().apply {
        setURL(url)
    }

    val userService = UsersService(JdbcUserRepo(jdbc))
    val matchService = MatchService(JdbcMatchRepo(jdbc))
    configureWebSocket(matchService, userService)
    configureCors()
    configureSerialization()
    configureRouting(userService, matchService)
}


