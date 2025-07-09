package com.crossBoard.httpModelTests


import com.crossBoard.httpModel.ErrorMessage
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ErrorMessageTest {

    private val json = Json { encodeDefaults = true }

    @Test
    fun `create ErrorMessage with a message`() {
        val error = ErrorMessage("Something went wrong")
        assertEquals("Something went wrong", error.message)
    }

    @Test
    fun `serialize ErrorMessage to JSON`() {
        val error = ErrorMessage("Invalid request")
        val jsonString = json.encodeToString(ErrorMessage.serializer(), error)
        assertEquals("""{"message":"Invalid request"}""", jsonString)
    }

    @Test
    fun `deserialize JSON to ErrorMessage`() {
        val jsonString = """{"message":"Not found"}"""
        val error = json.decodeFromString(ErrorMessage.serializer(), jsonString)

        assertNotNull(error)
        assertEquals("Not found", error.message)
    }
}
