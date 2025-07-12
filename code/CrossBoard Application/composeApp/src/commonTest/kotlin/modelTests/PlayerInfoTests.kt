package modelTests

import com.crossBoard.model.PlayerInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PlayerInfoTests {

    @Test fun playerInfoTests() {
        val result = PlayerInfo(
            id = null,
            username = "testUser"
        )

        assertNull(result.id)
        assertEquals("testUser", result.username)
        assertEquals("testUser", result.toString())

        val result2 = PlayerInfo(
            id = 1,
            username = "anotherUser"
        )

        assertEquals(1, result2.id)
        assertEquals("anotherUser", result2.username)
        assertEquals("anotherUser", result2.toString())
    }

}