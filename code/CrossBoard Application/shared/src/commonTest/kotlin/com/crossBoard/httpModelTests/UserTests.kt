package com.crossBoard.httpModelTests


import com.crossBoard.httpModel.UserCreationInput
import com.crossBoard.httpModel.UserCreationOutput
import com.crossBoard.httpModel.UserLoginInput
import com.crossBoard.httpModel.UserLoginOutput
import com.crossBoard.httpModel.UserProfileOutput
import com.crossBoard.httpModel.UserUpdateInput
import kotlinx.serialization.json.Json
import kotlin.test.*

class UserModelsTest {

    private val json = Json { encodeDefaults = true }

    @Test
    fun `create UserCreationInput with valid values`() {
        val input = UserCreationInput("testuser", "test@example.com", "password123")
        assertEquals("testuser", input.username)
        assertEquals("test@example.com", input.email)
        assertEquals("password123", input.password)
    }

    @Test
    fun `serialize and deserialize UserCreationOutput`() {
        val output = UserCreationOutput(42, "token123")
        val serialized = json.encodeToString(UserCreationOutput.serializer(), output)
        val deserialized = json.decodeFromString(UserCreationOutput.serializer(), serialized)

        assertEquals(output, deserialized)
    }

    @Test
    fun `serialize and deserialize UserProfileOutput`() {
        val profile = UserProfileOutput(1, "alice", "alice@example.com", "abc123", "active")
        val serialized = json.encodeToString(UserProfileOutput.serializer(), profile)
        val deserialized = json.decodeFromString(UserProfileOutput.serializer(), serialized)

        assertEquals(profile, deserialized)
    }

    @Test
    fun `create UserUpdateInput with optional fields`() {
        val update = UserUpdateInput(username = "newname")
        assertEquals("newname", update.username)
        assertNull(update.email)
        assertNull(update.password)
    }

    @Test
    fun `serialize and deserialize UserLoginInput`() {
        val input = UserLoginInput("bob", "securePass")
        val serialized = json.encodeToString(UserLoginInput.serializer(), input)
        val deserialized = json.decodeFromString(UserLoginInput.serializer(), serialized)

        assertEquals(input, deserialized)
    }

    @Test
    fun `serialize and deserialize UserLoginOutput`() {
        val output = UserLoginOutput(10, "jwt.token.value", "bob@example.com", "online")
        val serialized = json.encodeToString(UserLoginOutput.serializer(), output)
        val deserialized = json.decodeFromString(UserLoginOutput.serializer(), serialized)

        assertEquals(output, deserialized)
    }
}
