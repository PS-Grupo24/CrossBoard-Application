package com.crossBoard.httpModelTests

import com.crossBoard.domain.Column
import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.MultiPlayerMatch
import com.crossBoard.domain.Player
import com.crossBoard.domain.Row
import com.crossBoard.domain.Square
import com.crossBoard.domain.board.TicTacToeBoard
import com.crossBoard.domain.board.TicTacToeBoardRun
import com.crossBoard.domain.board.initialTicTacToePositions
import com.crossBoard.domain.matchModule.ModuleProvider
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.httpModel.*
import com.crossBoard.httpModel.moveHttp.TicTacToeMoveHttp
import kotlin.test.*

class MatchTests {

    private val user1Id = 1
    private val user2Id = 2
    private val move = TicTacToeMove(Player.BLACK, Square(Row(0, TicTacToeBoard.BOARD_DIM), Column('a' + 0)))
    private val board = TicTacToeBoardRun(
        positions = initialTicTacToePositions(),
        moves = listOf(move),
        turn = Player.BLACK,
        player1 = Player.BLACK,
        player2 = Player.WHITE
    )

    private val match = MultiPlayerMatch(
        board = board,
        id = 123,
        state = MatchState.RUNNING,
        user1 = user1Id,
        user2 = user2Id,
        matchType = MatchType.TicTacToe,
        version = 5,
        winner = null
    )

    @Test
    fun `toMatchOutput should produce expected MatchOutput`() {
        val output = match.toMatchOutput()

        assertEquals(match.id, output.matchId)
        assertEquals("TicTacToe", output.matchType)
        assertEquals("RUNNING", output.state)
        assertEquals(user1Id, output.user1Info)
        assertEquals(user2Id, output.user2Info)
        assertEquals(match.version, output.version)
        assertEquals(null, output.winner)
        assertNotNull(output.board)
    }

    @Test
    fun `toMultiplayerMatch should convert MatchOutput back to MultiPlayerMatch`() {
        val matchOutput = match.toMatchOutput()
        val result = matchOutput.toMultiplayerMatch()

        assertEquals(match.id, result.id)
        assertEquals(match.version, result.version)
        assertEquals(match.user1, result.user1)
        assertEquals(match.user2, result.user2)
        assertEquals(match.matchType, result.matchType)
        assertEquals(match.state, result.state)
        assertEquals(match.winner, result.winner)
        assertEquals(match.getPlayerType(user1Id), result.getPlayerType(user1Id))
    }

    @Test
    fun `toPlayedMatch should return MatchPlayedOutput with correct move`() {
        val playedOutput = match.toPlayedMatch()

        assertEquals(match.version, playedOutput.version)

        val moveOutput = playedOutput.move as TicTacToeMoveHttp
        assertEquals("BLACK", moveOutput.player)
        assertEquals("3a", moveOutput.square)
    }

    @Test fun `Test`(){
        val match = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)
        val joined = match.join(2)

        val matchOutput = joined.toMatchOutput()
        val result = matchOutput.toMultiplayerMatch()
        val module = ModuleProvider.getModule(match.matchType)
        assertNotNull(module)
        assertEquals(Player.EMPTY,result.board.get(module.getSquare(0,0)) )
    }
}
