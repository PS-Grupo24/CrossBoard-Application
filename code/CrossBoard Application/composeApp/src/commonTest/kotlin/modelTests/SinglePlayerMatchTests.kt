package modelTests

import com.crossBoard.domain.*
import com.crossBoard.domain.board.TicTacToeBoard
import com.crossBoard.domain.board.TicTacToeBoardRun
import com.crossBoard.domain.board.TicTacToeBoardWin
import com.crossBoard.domain.board.initialTicTacToePositions
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.model.SinglePlayerMatch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class SinglePlayerMatchTests {

    @Test fun singlePlayerMatchTest() {

        val board = TicTacToeBoardRun(initialTicTacToePositions(), emptyList<TicTacToeMove>(), Player.BLACK, Player.WHITE, Player.BLACK)

        val singlePlayerMatch = SinglePlayerMatch(
            id = 1,
            board = board,
            state = MatchState.RUNNING,
            matchType = MatchType.TicTacToe,
            version = 1
        )

        assertEquals(1, singlePlayerMatch.id)
        assertEquals(board, singlePlayerMatch.board)
        assertEquals(MatchState.RUNNING, singlePlayerMatch.state)
        assertEquals(MatchType.TicTacToe, singlePlayerMatch.matchType)
        assertEquals(1, singlePlayerMatch.version)
    }

    @Test fun startGameTest() {
        val board = TicTacToeBoardRun(initialTicTacToePositions(), emptyList<TicTacToeMove>(), Player.BLACK, Player.WHITE, Player.BLACK)

        val matchType = MatchType.TicTacToe
        val singlePlayerMatch = SinglePlayerMatch.startGame(matchType)

        assertEquals(board, singlePlayerMatch.board)
        assertEquals(MatchState.RUNNING, singlePlayerMatch.state)
        assertEquals(matchType, singlePlayerMatch.matchType)
        assertEquals(1, singlePlayerMatch.version)
    }

    @Test fun makeMoveTest() {
        val initialBoard = TicTacToeBoardRun(initialTicTacToePositions(), emptyList<TicTacToeMove>(), Player.BLACK, Player.WHITE, Player.BLACK)
        val singlePlayerMatch = SinglePlayerMatch(
            id = 1,
            board = initialBoard,
            state = MatchState.RUNNING,
            matchType = MatchType.TicTacToe,
            version = 1
        )

        val move = TicTacToeMove(Player.BLACK, Square(Row(0, TicTacToeBoard.BOARD_DIM), Column('a')))
        val newMatch = singlePlayerMatch.makeMove(move)

        assertEquals(1, newMatch.id)
        assertEquals(MatchState.RUNNING, newMatch.state)
        assertEquals(MatchType.TicTacToe, newMatch.matchType)
        assertEquals(2, newMatch.version)
        assertEquals(listOf<TicTacToeMove>(move), newMatch.board.moves)
    }

    @Test fun forfeit() {
        val initialBoard = TicTacToeBoardRun(initialTicTacToePositions(), emptyList<TicTacToeMove>(), Player.BLACK, Player.WHITE, Player.BLACK)
        val singlePlayerMatch = SinglePlayerMatch(
            id = 1,
            board = initialBoard,
            state = MatchState.RUNNING,
            matchType = MatchType.TicTacToe,
            version = 1
        )

        val player = Player.BLACK
        val newMatch = singlePlayerMatch.forfeit(player)

        assertEquals(1, newMatch.id)
        assertEquals(MatchState.WIN, newMatch.state)
        assertEquals(MatchType.TicTacToe, newMatch.matchType)
        assertEquals(2, newMatch.version)
        assertEquals(Player.WHITE, (newMatch.board as TicTacToeBoardWin).winner)
    }

    @Test fun equalsAndHashTest() {
        val board = TicTacToeBoardRun(initialTicTacToePositions(), emptyList<TicTacToeMove>(), Player.BLACK, Player.WHITE, Player.BLACK)

        val match1 = SinglePlayerMatch(
            id = 1,
            board = board,
            state = MatchState.RUNNING,
            matchType = MatchType.TicTacToe,
            version = 1
        )

        val match2 = SinglePlayerMatch(
            id = 1,
            board = board,
            state = MatchState.RUNNING,
            matchType = MatchType.TicTacToe,
            version = 1
        )

        assertEquals(match1, match2)
        assertEquals(match1.hashCode(), match2.hashCode())

        val match3 = SinglePlayerMatch(
            id = 2,
            board = board,
            state = MatchState.RUNNING,
            matchType = MatchType.TicTacToe,
            version = 1
        )

        assertFalse(match1.equals(match3))
        assertNotEquals(match1.hashCode(), match3.hashCode())
    }
}