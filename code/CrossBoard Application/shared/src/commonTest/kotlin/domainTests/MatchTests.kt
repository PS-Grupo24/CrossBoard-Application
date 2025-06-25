package domainTests
import com.crossBoard.domain.*
import com.crossBoard.domain.move.Move
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.domain.move.moveToString
import com.crossBoard.httpModel.*
import kotlin.test.*

class MatchTests {

    @Test fun startMultiplayerMatchInvalidTest() {
        assertFailsWith<IllegalArgumentException> {
            MultiPlayerMatch.startGame(0, MatchType.TicTacToe)
        }
    }

    @Test fun startMultiplayerMatchValidTest() {
        val expectedMatch = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)

        val player2 = expectedMatch.user2
        val matchId = expectedMatch.id
        val board = expectedMatch.board
        val boardState = getMatchStateFromBoard(player2, board)

        assertEquals(board, expectedMatch.board)
        assertEquals(matchId, expectedMatch.id)
        assertEquals(boardState, expectedMatch.state)
        assertEquals(1, expectedMatch.user1)
        assertNull(expectedMatch.user2)
        assertEquals(MatchType.TicTacToe, expectedMatch.matchType)
        assertEquals(1, expectedMatch.version)
    }

    @Test fun joinMultiplayerMatchWithoutSuccessTest() {
        val match = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)

        assertFailsWith<IllegalArgumentException> {
            match.join(0)
        }

        assertFailsWith<IllegalArgumentException> {
            match.join(1)
        }

        assertFailsWith<IllegalArgumentException> {
            val newMatch = match.join(2)
            newMatch.join(3)
        }
    }

    @Test fun joinMultiplayerMatchWithSuccessTest() {
        val match = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)
        val newMatch = match.join(2)

        assertEquals(match.board, newMatch.board)
        assertEquals(match.id, newMatch.id)
        assertEquals(1, match.user1)
        assertEquals(2, newMatch.user2)
        assertEquals(MatchType.TicTacToe, newMatch.matchType)
        assertEquals(MatchState.RUNNING, newMatch.state)
        assertEquals(2, newMatch.version)
    }

    @Test fun playMultiplayerMatchWithSuccessTest() {
        val match = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)
        val newMatch = match.join(2)

        val playerTurn = newMatch.board.turn

        val move = TicTacToeMove(playerTurn, Square(Row(1, 3), Column('a')))

        val updatedMatch = newMatch.play(move)

        assertEquals(List<Move>(1){move}, updatedMatch.board.moves)
        assertEquals(newMatch.id, updatedMatch.id)
        assertEquals(MatchState.RUNNING, updatedMatch.state)
        assertEquals(newMatch.user1, updatedMatch.user1)
        assertEquals(newMatch.user2, updatedMatch.user2)
        assertEquals(MatchType.TicTacToe, updatedMatch.matchType)
        assertEquals(3, updatedMatch.version)
    }

    @Test fun forfeitMultiplayerMatchWithoutSuccess() {
        assertFailsWith<IllegalArgumentException> {
            val match = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)
            match.forfeit(0)
        }
    }

    @Test fun forfeitMultiplayerMatchWithSuccess() {
        val match = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)
        val newMatch = match.join(2)

        val updatedMatch = newMatch.forfeit(1)

        assertEquals(match.board, updatedMatch.board)
        assertEquals(match.id, updatedMatch.id)
        assertEquals(MatchState.WIN, updatedMatch.state)
        assertEquals(newMatch.user1, updatedMatch.user1)
        assertEquals(newMatch.user2, updatedMatch.user2)
        assertEquals(MatchType.TicTacToe, updatedMatch.matchType)
        assertEquals(3, updatedMatch.version)
    }

    @Test fun getPlayerTypeWithoutSuccess() {
        val match = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)

        assertFailsWith<IllegalArgumentException> {
            match.getPlayerType(2)
        }
    }

    @Test fun getPlayerTypeWithSuccess() {
        val match = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)
        val newMatch = match.join(2)

        val typePlayer = if(newMatch.board.player1.name == "BLACK") Player.BLACK else Player.WHITE
        assertEquals(typePlayer, newMatch.getPlayerType(1))
        assertEquals(typePlayer.other(), newMatch.getPlayerType(2))
    }

    @Test fun isMyTurnWithoutSuccess() {

        val match = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)

        assertFailsWith<IllegalArgumentException> {
            match.isMyTurn(0)
        }

        assertFailsWith<IllegalArgumentException> {
            match.isMyTurn(2)
        }
    }

    @Test fun isMyTurnWithSuccess() {
        val match = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)
        val newMatch = match.join(2)

        assertTrue(newMatch.user2 != null)

        val turn = match.board.turn
        val turnId = if(turn == newMatch.getPlayerType(1)) newMatch.user1 else newMatch.user2!!

        assertTrue(newMatch.isMyTurn(turnId))
        assertFalse(newMatch.isMyTurn(newMatch.otherPlayer(turnId)))
    }

    @Test fun otherPlayerWithoutSuccess() {
        val match = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)

        assertFailsWith<IllegalArgumentException> {
            match.otherPlayer(0)
        }

        assertFailsWith<IllegalArgumentException> {
            match.otherPlayer(2)
        }

        assertFailsWith<IllegalArgumentException> {
            match.otherPlayer(1)
        }
    }

    @Test fun otherPlayerWithSuccess() {
        val match = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)
        val newMatch = match.join(2)

        assertEquals(newMatch.user2, newMatch.otherPlayer(newMatch.user1))
        assertEquals(newMatch.user1, newMatch.otherPlayer(newMatch.user2!!))
    }

    @Test fun toMatchOutputWithSuccess() {
        val match = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)
        val newMatch = match.join(2)

        val boardOutput = BoardOutput(null, newMatch.board.turn.toString(), newMatch.board.positions.map {it.toString()}, newMatch.board.moves.map {
            moveToString(it)
        })
        val expectedMatchOutput = MatchOutput(
            newMatch.id, PlayerOutput(1, newMatch.board.player1.name), PlayerOutput(2, newMatch.board.player2.name),
            boardOutput, newMatch.matchType.toString(), newMatch.version, newMatch.state.toString()
        )

        val result = newMatch.toMatchOutput()

        assertEquals(newMatch.id, result.matchId)
        assertEquals(newMatch.user1, result.user1Info.userId)
        assertEquals(newMatch.user2, result.user2Info.userId)
        assertEquals("null", result.board.winner)
        assertEquals(expectedMatchOutput.board.turn, result.board.turn)
        assertEquals(expectedMatchOutput.board.moves, result.board.moves)
        assertEquals(expectedMatchOutput.board.positions, result.board.positions)
        assertEquals(expectedMatchOutput.board.moves, result.board.moves)
        assertEquals(expectedMatchOutput.matchType, result.matchType)
        assertEquals(expectedMatchOutput.version, result.version)
        assertEquals(expectedMatchOutput.state, result.state)
    }

    @Test fun toPlayedMatchWithSuccess() {
        val match = MultiPlayerMatch.startGame(1, MatchType.TicTacToe)

        val newMatch = match.join(2)

        val playerTurn = newMatch.board.turn

        val move = TicTacToeMove(playerTurn, Square(Row(1, 3), Column('a')))

        val updatedMatch = newMatch.play(move)

        val expectedResult = MatchPlayedOutput(move.toMoveOutput(), 3)

        val result = updatedMatch.toPlayedMatch()

        assertEquals(expectedResult, result)
        assertEquals(updatedMatch.version, result.version)
        assertEquals(updatedMatch.board.moves[0].toMoveOutput(), result.move)
    }
}