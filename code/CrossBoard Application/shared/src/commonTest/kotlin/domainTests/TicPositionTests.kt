package domainTests

import com.crossBoard.domain.*
import com.crossBoard.domain.position.TicPosition
import com.crossBoard.domain.position.toPosition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TicPositionTests {
    @Test fun creatingTicPosition() {
        val ticPosition = TicPosition(Player.BLACK, Square(Row(1, 3), Column('a')))

        assertEquals(Player.BLACK.name, ticPosition.player.name)
        assertEquals(2, ticPosition.square.row.number)
        assertEquals(1, ticPosition.square.row.index)
        assertEquals('a', ticPosition.square.column.symbol)
        assertEquals(0, ticPosition.square.column.index)
    }

    @Test fun creatingTicPositionString() {
        val ticPosition = TicPosition(Player.BLACK, Square(Row(2, 3), Column('a')))

        val stringTicPosition = ticPosition.toString()

        assertEquals("BLACK,1a", stringTicPosition)
    }

    @Test fun creatingTicPositionWithValidString() {
        val stringTicPosition = "BLACK,1a".toPosition(3, MatchType.TicTacToe)

        val ticPosition = TicPosition(Player.BLACK, Square(Row(2, 3), Column('a')))

        assertTrue(stringTicPosition is TicPosition)
        assertEquals(ticPosition.player.name, stringTicPosition.player.name)
        assertEquals(ticPosition.square.row.number, stringTicPosition.square.row.number)
        assertEquals(ticPosition.square.row.index, stringTicPosition.square.row.index)
        assertEquals(ticPosition.square.column.symbol, stringTicPosition.square.column.symbol)
        assertEquals(ticPosition.square.column.index, stringTicPosition.square.column.index)
    }
}