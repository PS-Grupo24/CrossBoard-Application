import domain.Player
import domain.TicTacToeBoardRun
import domain.initialTicTacToePositions
import model.GameType
import model.MultiPlayerMatch

import kotlin.test.Test
import kotlin.test.assertEquals

class MatchTests {

    @Test fun gameTypeTests() {
        val gametype = GameType.TicTacToe

        assertEquals("TicTacToe", gametype.name)
    }

    @Test fun multiplayermatchStartGameTest() {

        val player = Player.random()
        val board = TicTacToeBoardRun(
            initialTicTacToePositions(),
            emptyList(),
            Player.random(),
            player,
            player.other()
        )
        val newMatch = MultiPlayerMatch.startGame(1U, GameType.TicTacToe)

        assertEquals(board, newMatch.board)
        assertEquals(1U, newMatch.player1)
        assertEquals(null, newMatch.player2)
        assertEquals(GameType.TicTacToe, newMatch.gameType)
    }
}