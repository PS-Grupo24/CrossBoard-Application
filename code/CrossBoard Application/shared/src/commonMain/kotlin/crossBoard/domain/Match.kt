package crossBoard.domain

import crossBoard.httpModel.*
import kotlin.random.Random

/**
 * Interface "Game" represents a game.
 * @property id the id of the game.
 * @property board the board of the game.
 * @property getPlayerType Function to get the player type.
 */
interface Match {
    val id: Int
    val board: Board
    val state: MatchState
    fun getPlayerType(userId: Int): Player
}

/**
 * Class "MultiPlayerGame" represents a multiplayer game.
 * @param board the board of the game.
 * @param id the id of the match.
 * @param player1 the first player.
 * @param player2 the second player.
 * @param matchType the type of the match.
 * @return Match the match that was created as a multiplayer game.
 */
data class MultiPlayerMatch(
    override val board: Board,
    override val id: Int,
    override val state: MatchState,
    val player1: Int,
    val player2: Int? = null,
    val matchType: MatchType,
    val version: Int,
): Match {
    companion object{
        /**
         * Function startGame responsible to start a game.
         * @param player1 the first player.
         * @param matchType the type of the game.
         * @return Game the game that was created.
         */
        fun startGame(player1: Int, matchType: MatchType): MultiPlayerMatch = when(matchType){
            MatchType.TicTacToe -> {
                val p1 = Player.random()
                val board = TicTacToeBoardRun(
                    initialTicTacToePositions(),
                    emptyList(),
                    Player.random(),
                    p1,
                    p1.other(),
                )
                MultiPlayerMatch(
                    board,
                    Random.nextInt(from = 1, Int.MAX_VALUE),
                    getMatchStateFromBoard(board),
                    player1,
                    null,
                    matchType,
                    1
                )
            }
        }
    }
    /**
     * Function play responsible to play a move on the board.
     * @param move The move to be made.
     * @return MultiPlayerGame the game after the move was played.
     */
    fun play(move: Move): MultiPlayerMatch {
        val newBoard = board.play(move)
        return MultiPlayerMatch(
            newBoard,
            id,
            getMatchStateFromBoard(newBoard),
            player1,
            player2,
            matchType,
            version + 1
        )
    }

    fun join(userId2: Int): MultiPlayerMatch {
        require(userId2 > 0) { "userId2 must be greater than 0" }
        require(player2 == null) { "This game is full" }
        require(userId2 != player1) { "Player2 can't be the same as Player1" }
        return MultiPlayerMatch(
            board, id, state, player1, userId2, matchType, version + 1
        )
    }

    /**
     * Function forfeit responsible to forfeit the game.
     * @param player the player that is forfeiting.
     * @return MultiPlayerGame the game after the forfeit.
     */
    fun forfeit(player: Int): MultiPlayerMatch {
        val playerType = getPlayerType(player)
        val newBoard = board.forfeit(playerType)
        return MultiPlayerMatch(newBoard, id, getMatchStateFromBoard(newBoard), player1, player2,matchType, version + 1)
    }

    /**
     * Function getPlayerType responsible to get the player type.
     * @param userId the player.
     * @return Player the player type.
     */
    override fun getPlayerType(userId: Int): Player {
        require(userId == player1 || userId == player2) {"This user is not a player in this match"}
        return when(userId){
            player1 -> board.player1
            else -> board.player2
        }
    }



    override fun equals(other: Any?) = other is MultiPlayerMatch && id == other.id && other.version == version
    override fun hashCode(): Int {
        return id.hashCode() + version.hashCode()
    }
}



fun MultiPlayerMatch.toMatchOutput() : MatchOutput {
    val winner = if (board is BoardWin) board.winner.toString() else null
    return MatchOutput(
        id,
        PlayerOutput(
            player1,
            getPlayerType(player1).toString()
        ),
        PlayerOutput(
            player2,
            getPlayerType(player1).other().toString()
        ),
        BoardOutput(
            winner,
            board.turn.toString(),
            board.positions.map { it.toString() },
            board.moves.map { moveToString(it) },
        ),
        matchType.toString(),
        version,
        state.toString(),
    )
}

fun MultiPlayerMatch.toPlayedMatch() = MatchPlayedOutput(this.board.moves.last().toMoveOutput(), this.version)

/**
 * Class "SinglePlayerGame" represents a single player game.
 * @param board the board of the game.
 * @param user the player.
 * @param difficulty the difficulty of the game.
 * @return Game the game that was created as a single player game.
 */
class SinglePlayerMatch(
    override val board: Board,
    override val id: Int,
    override val state: MatchState,
    val user: UInt,
    difficulty: Difficulty
):
    Match {
    override fun getPlayerType(userId: Int): Player = board.player1

}