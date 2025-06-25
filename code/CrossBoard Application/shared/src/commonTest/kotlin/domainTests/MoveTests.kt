package domainTests

import com.crossBoard.domain.*
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.domain.move.moveToString
import com.crossBoard.domain.move.toMove
import com.crossBoard.httpModel.TicTacToeMoveOutput
import kotlin.test.Test
import kotlin.test.assertEquals

class MoveTests {
    @Test fun moveToStringWithSucesss() {
        val move = TicTacToeMove(Player.BLACK, Square(Row(0, 3), Column('a')))

        assertEquals("BLACK,3a", moveToString(move))
    }

    @Test fun toMoveWithSuccess() {

        val move = TicTacToeMove(Player.BLACK, Square(Row(0, 3), Column('a')))

        assertEquals(move, "BLACK,3a".toMove(MatchType.TicTacToe))
    }

    @Test fun toMoveWithSuccessUsingMoveOutput() {
        val move = TicTacToeMove(Player.BLACK, Square(Row(0, 3), Column('a')))

        val moveOutput = TicTacToeMoveOutput("BLACK", "3a")

        assertEquals(move, moveOutput.toMove())
    }
}