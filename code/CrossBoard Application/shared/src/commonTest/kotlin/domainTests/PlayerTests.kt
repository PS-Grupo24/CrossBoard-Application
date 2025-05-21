package domainTests

import com.crossBoard.domain.Player
import com.crossBoard.domain.toPlayer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PlayerTests {

    @Test fun playerEnumClassTest() {
        val player = Player.WHITE

        assertEquals(player.name, "WHITE")
        assertEquals(player.ordinal, 0)

        val player2 = Player.BLACK

        assertEquals(player2.name, "BLACK")
        assertEquals(player2.ordinal, 1)

        val player3 = Player.EMPTY

        assertEquals(player3.name, "EMPTY")
        assertEquals(player3.ordinal, 2)
    }

    @Test fun otherPlayerWithSuccess() {
        val player = Player.WHITE

        assertEquals(Player.BLACK, player.other())

        val player2 = Player.BLACK

        assertEquals(Player.WHITE, player2.other())

        val player3 = Player.EMPTY

        assertEquals(Player.EMPTY, player3.other())
    }

    @Test fun toStringTest() {
        val player = Player.WHITE

        assertEquals(player.toString(), "WHITE")

        val player2 = Player.BLACK

        assertEquals(player2.toString(), "BLACK")

        val player3 = Player.EMPTY

        assertEquals(player3.toString(), "EMPTY")
    }

    @Test fun toPlayerTest() {
        val player = Player.BLACK

        assertEquals(player, "BLACK".toPlayer())

        val player2 = Player.WHITE

        assertEquals(player2, "WHITE".toPlayer())

        val player3 = Player.EMPTY

        assertEquals(player3, "EMPTY".toPlayer())

        assertFailsWith<IllegalArgumentException> {
            "INVALID".toPlayer()
        }
    }
}