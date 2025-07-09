import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.UserState
import com.crossBoard.httpModel.ErrorMessage
import com.crossBoard.httpModel.MatchCancel
import com.crossBoard.httpModel.MatchOutput
import com.crossBoard.httpModel.UserCreationInput
import com.crossBoard.httpModel.UserCreationOutput
import com.crossBoard.httpModel.UserLoginInput
import com.crossBoard.httpModel.UserLoginOutput
import com.crossBoard.httpModel.UserProfileOutput
import com.crossBoard.httpModel.UserUpdateInput
import com.crossBoard.httpModel.toMultiplayerMatch
import com.crossBoard.testModule
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.assertNull
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

        val user = createUser(client, "johndoe", "john@example.com", "SecurePassword123!")
        assertTrue(user.token.isNotBlank(), "The returned token should not be blank")
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

    @Test
    fun `should return users matching username fragment`() = testApplication {
        application {
            testModule()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val john = createUser(client,"johndoe", "john@example.com", "SecurePassword123!")
        val response = client.get("/user/username/john") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${john.token}")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val users = response.body<List<UserProfileOutput>>()
        assertTrue(users.isNotEmpty(), "Should return at least one matching user")
        assertTrue(users.all { it.username.contains("john", ignoreCase = true) })
    }

    @Test
    fun `should return unauthorized for missing token`() = testApplication {
        application {
            testModule()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.get("/user/username/john") {
            contentType(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        val error = response.body<ErrorMessage>()
        assertTrue(error.message.contains("Missing token"))
    }

    @Test
    fun `should return bad request for missing username`() = testApplication {
        application {
            testModule()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.get("/user/username/") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer VALID_TOKEN")
        }
        assertTrue(response.status == HttpStatusCode.BadRequest || response.status == HttpStatusCode.NotFound)
    }

    @Test
    fun `should return empty list for no matches`() = testApplication {
        application {
            testModule()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val john = createUser(client, "johndoe", "john@email.com", "SecurePassword123!")
        val response = client.get("/user/username/noSuchUserXYZ") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${john.token}")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val users = response.body<List<UserProfileOutput>>()
        assertTrue(users.isEmpty(), "Should return an empty list for no matches")
    }

    @Test
    fun `should return partial result with skip and limit`() = testApplication {
        application {
            testModule()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val john = createUser(client, "johndoe", "john@email.com", "SecurePassword123!")
        val john2 = createUser(client, "johndoe2", "john2@email.com", "SecurePassword123!")
        val response = client.get("/user/username/john?skip=0&limit=1") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${john.token}")
        }
        //should be ok with only the first john found
        assertEquals(HttpStatusCode.OK, response.status)
        val users = response.body<List<UserProfileOutput>>()
        assertTrue(users.size <= 1, "Should return at most 1 user")
        assertEquals(users.first().id, john.id)

        val response2 = client.get("/user/username/john?skip=0&limit=2") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${john.token}")
        }
        //should be ok with both johns found
        assertEquals(HttpStatusCode.OK, response2.status)
        val users2 = response2.body<List<UserProfileOutput>>()
        assertTrue(users2.size == 2, "Should return 2 users")

        val response3 = client.get("/user/username/john?skip=1&limit=2") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${john.token}")
        }

        //should be ok with only the second john found
        assertEquals(HttpStatusCode.OK, response3.status)
        val users3 = response3.body<List<UserProfileOutput>>()
        assertTrue(users3.size == 1, "Should return 1 user")
        assertEquals(users3.first().id, john2.id)
    }

    @Test fun `Test correct login`() = testApplication {
        application {
            testModule()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val john = createUser(client, "johndoe", "john@email.com", "SecurePassword123!")
        val response = client.post("/user/login"){
            contentType(ContentType.Application.Json)
            setBody(UserLoginInput("johndoe","SecurePassword123!" ))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val loggedInUser = response.body<UserLoginOutput>()
        assertEquals( john.id, loggedInUser.id)
        assertEquals( john.token, loggedInUser.token)
        assertEquals(UserState.NORMAL.name, loggedInUser.state)
        assertEquals("john@email.com", loggedInUser.email)
    }

    @Test fun `Test incorrect login`() = testApplication {
        application {
            testModule()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        createUser(client, "johndoe", "john@email.com", "SecurePassword123!")
        val response = client.post("/user/login"){
            contentType(ContentType.Application.Json)
            setBody(UserLoginInput("incorrectusername","SecurePassword123!" ))
        }
        assertEquals(HttpStatusCode.NotFound, response.status)

        val response2 = client.post("/user/login"){
            contentType(ContentType.Application.Json)
            setBody(UserLoginInput("johndoe","SecurePassword123" ))
        }
        assertEquals(HttpStatusCode.Conflict, response2.status)
        val error = response2.body<ErrorMessage>()
        assertEquals(error.message, "Wrong password")
    }

    @Test fun `Test get user by token`() = testApplication {
        application {
            testModule()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        //should not find a user yet.
        val response = client.get("/user"){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer NONE-EXISTENT-TOKEN")
        }
        assertEquals(HttpStatusCode.NotFound, response.status)

        val john = createUser(client, "johndoe", "john@email.com", "SecurePassword123!")

        //should find a user
        val response2 = client.get("/user"){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${john.token}")
        }
        assertEquals(HttpStatusCode.OK, response2.status)
        val found = response2.body<UserProfileOutput>()
        assertEquals(john.id, found.id)
        assertEquals(john.token, found.token)
        assertEquals(UserState.NORMAL.name, found.state)
        assertEquals("john@email.com", found.email)
        assertEquals("johndoe",found.username)
    }

    @Test fun `Test update user`() = testApplication {
        application {
            testModule()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val john = createUser(client, "johndoe", "john@email.com", "SecurePassword123!")
        createUser(client, "Alice", "alice@hotmail.com", "SecurePassword123!")
        //should be success
        val response = client.put("/user"){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${john.token}")
            setBody(UserUpdateInput(username = "johndoe2", password = "SecurePassword456?"))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val updated = response.body<UserProfileOutput>()
        assertEquals(john.id, updated.id)
        assertEquals(john.token, updated.token)
        assertEquals(UserState.NORMAL.name, updated.state)
        assertEquals("johndoe2", updated.username)

        //should be error for name conflict
        val response2 = client.put("/user"){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${john.token}")
            setBody(UserUpdateInput(username = "Alice", password = "SecurePassword456?"))
        }
        assertEquals(HttpStatusCode.Conflict, response2.status)
        val error2 = response2.body<ErrorMessage>()
        assertEquals(error2.message, "Username already exists")

        //should be error for email conflict
        val response3 = client.put("/user"){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${john.token}")
            setBody(UserUpdateInput(email = "alice@hotmail.com"))
        }
        assertEquals(HttpStatusCode.Conflict, response3.status)
        val error3 = response3.body<ErrorMessage>()
        assertEquals(error3.message, "Email already exists")

        //should be an error if one of the new properties doesn't pass the domain requirements
        val response4 = client.put("/user"){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${john.token}")
            setBody(UserUpdateInput(email = "alicehotmail.com"))
        }
        assertEquals(HttpStatusCode.BadRequest, response4.status)
        val error4 = response4.body<ErrorMessage>()
        assertEquals(error4.message, "The email must contain \"@\"")
    }

    @Test fun `Test get user by id`() = testApplication {
        application {
            testModule()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        //should be not found cause id doesn't exist
        val response = client.get("/user/4")
        assertEquals(HttpStatusCode.NotFound, response.status)


        //should find the new id
        val john = createUser(client, "johndoe", "john@email.com", "SecurePassword123!")
        val response2 = client.get("/user/${john.id}")

        assertEquals(HttpStatusCode.OK, response2.status)
        val found = response2.body<UserProfileOutput>()
        assertEquals(john.id, found.id)
        assertEquals(john.token, found.token)
        assertEquals(UserState.NORMAL.name, found.state)
        assertEquals("johndoe", found.username)
    }

    @Test fun `Test correct join match`() = testApplication {
        application {
            testModule()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val user1 = createUser(client, "user1", "user1@hotmail.com", "SecurePassword123!")
        val user2 = createUser(client, "user2", "user2@hotmail.com", "SecurePassword123!")

        val response1 = client.post("/match/${MatchType.TicTacToe.name}"){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${user1.token}")
        }

        //should create a match with user2 null
        assertEquals(HttpStatusCode.OK, response1.status)
        val match1 = response1.body<MatchOutput>().toMultiplayerMatch()
        assertEquals(user1.id, match1.user1)
        assertEquals(MatchState.WAITING, match1.state)
        assertNull(match1.user2)

        //should join the match and new user2
        val response2 = client.post("/match/${MatchType.TicTacToe.name}"){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${user2.token}")
        }

        assertEquals(HttpStatusCode.OK, response2.status)
        val match2 = response2.body<MatchOutput>().toMultiplayerMatch()
        assertEquals(user1.id, match2.user1)
        assertEquals(user2.id, match2.user2)
        assertEquals(match1.id, match2.id)
        assertEquals(MatchState.RUNNING, match2.state)
    }

    @Test fun `Test join different match`() = testApplication {
        application {
            testModule()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val user1 = createUser(client, "user1", "user1@hotmail.com", "SecurePassword123!")
        val user2 = createUser(client, "user2", "user2@hotmail.com", "SecurePassword123!")

        val response1 = client.post("/match/${MatchType.TicTacToe.name}"){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${user1.token}")
        }

        //should create a match with user2 null and match type tictactoe
        assertEquals(HttpStatusCode.OK, response1.status)
        val match1 = response1.body<MatchOutput>().toMultiplayerMatch()
        assertEquals(user1.id, match1.user1)
        assertEquals(MatchType.TicTacToe, match1.matchType)
        assertNull(match1.user2)

        //should create a different match
        val response2 = client.post("/match/${MatchType.Reversi.name}"){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${user2.token}")
        }
        assertEquals(HttpStatusCode.OK, response2.status)
        val match2 = response2.body<MatchOutput>().toMultiplayerMatch()
        assertEquals(user2.id, match2.user1)
        assertEquals(MatchType.Reversi, match2.matchType)
        assertNull(match2.user2)
    }

    @Test fun `Test joining 2 different matches`() = testApplication {
        application {
            testModule()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val user1 = createUser(client, "user1", "user1@hotmail.com", "SecurePassword123!")

        val response1 = client.post("/match/${MatchType.TicTacToe.name}"){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${user1.token}")
        }
        assertEquals(HttpStatusCode.OK, response1.status)

        val response2 = client.post("/match/${MatchType.TicTacToe.name}"){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${user1.token}")
        }
        assertEquals(HttpStatusCode.Conflict, response2.status)
        val error = response2.body<ErrorMessage>()
        assertEquals(error.message, "User already in an ongoing match")
    }

    @Test fun `Test forfeit`() = testApplication {
        application {
            testModule()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val user1 = createUser(client, "user1", "user1@hotmail.com", "SecurePassword123!")
        val user2 = createUser(client, "user2", "user2@hotmail.com", "SecurePassword123!")

        client.post("/match/${MatchType.TicTacToe.name}"){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${user1.token}")
        }

        val response1 = client.post("/match/${MatchType.TicTacToe.name}"){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${user2.token}")
        }
        val match = response1.body<MatchOutput>().toMultiplayerMatch()
        val response2 = client.post("/match/${match.id}/forfeit"){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${user2.token}")
        }
        assertEquals(HttpStatusCode.OK, response2.status)
        val forfeited = response2.body<MatchOutput>().toMultiplayerMatch()
        assertEquals(match.id, forfeited.id)
        assertEquals(MatchState.WIN, forfeited.state)
    }

    @Test fun `Test get match by id with success`() = testApplication {
        application {
            testModule()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val user1 = createUser(client, "user1", "user1@hotmail.com", "SecurePassword123!")

        val response = client.post("/match/${MatchType.TicTacToe.name}"){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${user1.token}")
        }
        val match = response.body<MatchOutput>().toMultiplayerMatch()

        val response2 = client.get("/match/${match.id}")
        assertEquals(HttpStatusCode.OK, response2.status)
        val found = response2.body<MatchOutput>().toMultiplayerMatch()
        assertEquals(match, found)
    }

    @Test fun `Test get match by id with failure`() = testApplication {
        application {
            testModule()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.get("/match/12")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test fun `Test Cancel match`() = testApplication {
        application {
            testModule()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val user1 = createUser(client, "user1", "user1@hotmail.com", "SecurePassword123!")

        val response1 = client.post("/match/${MatchType.TicTacToe.name}"){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${user1.token}")
        }
        val match = response1.body<MatchOutput>().toMultiplayerMatch()

        //Cancel the match
        val response2 = client.post("/match/${match.id}/cancel"){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${user1.token}")
        }
        assertEquals(HttpStatusCode.OK, response2.status)
        val cancel = response2.body<MatchCancel>()
        assertEquals(match.id, cancel.matchId)
        assertEquals(user1.id, cancel.userId)

        //should not find the match
        val response3 = client.get("/match/${match.id}")
        assertEquals(HttpStatusCode.NotFound, response3.status)
    }


}

private suspend fun createUser(client: HttpClient, username: String, email: String, password: String): UserCreationOutput{
    val input = UserCreationInput(username = username, email = email, password = password)

    val response = client.post("/user") {
        contentType(ContentType.Application.Json)
        setBody(input)
    }

    assertEquals(HttpStatusCode.Created, response.status)
    return response.body<UserCreationOutput>()
}