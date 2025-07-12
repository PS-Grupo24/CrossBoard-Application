package com.crossBoard.httpModelTests

import com.crossBoard.domain.Column
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.Player
import com.crossBoard.domain.Row
import com.crossBoard.domain.Square
import kotlin.test.*
import com.crossBoard.domain.board.*
import com.crossBoard.domain.move.*
import com.crossBoard.httpModel.moveHttp.ReversiMoveHttp
import com.crossBoard.httpModel.moveHttp.TicTacToeMoveHttp
import com.crossBoard.httpModel.moveHttp.toMove
import com.crossBoard.httpModel.moveHttp.toMoveHttp

class MoveTests {
    val player = Player.BLACK
    val revSquare = Square(Row(1, ReversiBoard.BOARD_DIM), Column('a' + 0))
    val ticSquare = Square(Row(1, TicTacToeBoard.BOARD_DIM), Column('a' + 0))

    val revMoveInput = ReversiMoveHttp(player.toString(), revSquare.toString())
    val ticMoveInput = TicTacToeMoveHttp(player.toString(), ticSquare.toString())

    val revMove = ReversiMove(player, revSquare)
    val ticMove = TicTacToeMove(player, ticSquare)

    @Test fun `Test toMove `() {
        val obtainedRevMove = revMoveInput.toMove(MatchType.Reversi)
        val obtainedTicMove = ticMoveInput.toMove(MatchType.TicTacToe)

        assertEquals(obtainedRevMove, revMove)
        assertEquals(obtainedTicMove, ticMove)
    }

    @Test fun `Test toMoveOutput`() {
        val revMoveOutput = revMove.toMoveHttp(MatchType.Reversi)
        val ticMoveOutput = ticMove.toMoveHttp(MatchType.TicTacToe)

        assertTrue(revMoveOutput is ReversiMoveHttp)
        assertTrue(ticMoveOutput is TicTacToeMoveHttp)

        assertEquals(revMoveOutput.player, player.toString())
        assertEquals(revMoveOutput.square, revSquare.toString())

        assertEquals(ticMoveOutput.player, player.toString())
        assertEquals(ticMoveOutput.square, ticSquare.toString())
    }


}
