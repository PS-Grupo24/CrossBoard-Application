import com.crossBoard.httpModel.ErrorMessage
import com.crossBoard.httpModel.UserCreationInput
import com.crossBoard.httpModel.UserCreationOutput
import com.crossBoard.testModule
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RoutingTests {
    @Test
    fun `should create user successfully`() = testApplication {
        application {
            testModule()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
}        }

        val input = UserCreationInput("johndoe", "john@example.com", "SecurePassword123!")

        val response = client.post("/user") {
            contentType(ContentType.Application.Json)
            setBody(input)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val body = response.body<UserCreationOutput>()
        assertTrue(body.token.isNotBlank(), "The returned token should not be blank")
    }

    @Test
    fun `should return bad request for invalid username`() = testApplication {
        application {
            testModule()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val invalidInput = """{ "username": "", "email": "bademail", "password": "" }"""

        val response = client.post("/user") {
            contentType(ContentType.Application.Json)
            setBody(invalidInput)
        }

        val error = response.body<ErrorMessage>()
        println(error.message)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(error.message.isNotBlank(), "The error message should not be blank")
    }

    @Test
    fun `should return bad request for invalid email`() = testApplication {
        application {
            testModule()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val invalidInput = """{ "username": "Ruben", "email": "bademail", "password": "" }"""

        val response = client.post("/user") {
            contentType(ContentType.Application.Json)
            setBody(invalidInput)
        }

        val error = response.body<ErrorMessage>()
        println(error.message)
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(error.message.isNotBlank(), "The error message should not be blank")
    }
}