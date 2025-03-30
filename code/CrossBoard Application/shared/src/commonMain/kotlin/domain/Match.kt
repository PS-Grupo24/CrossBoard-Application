package domain

import kotlin.random.Random

/**
 * Interface "Game" represents a game.
 * @property id the id of the game.
 * @property board the board of the game.
 * @property getPlayerType Function to get the player type.
 */
interface Match {
    val id: Int
    val board: Board;
    fun getPlayerType(userId: Int): Player
}

/**
 * Class "MultiPlayerGame" represents a multiplayer game.
 * @param board the board of the game.
 * @param id the id of the match.
 * @param player1 the first player.
 * @param player2 the second player.
 * @param gameType the type of the match.
 * @return Match the match that was created as a multiplayer game.
 */
data class MultiPlayerMatch(
    override val board: Board,
    override val id: Int,
    val player1: Int,
    val player2: Int? = null,
    val gameType: GameType
): Match {
    companion object{
        /**
         * Function startGame responsible to start a game.
         * @param player1 the first player.
         * @param gameType the type of the game.
         * @return Game the game that was created.
         */
        fun startGame(player1: Int, gameType: GameType): MultiPlayerMatch = when(gameType){
            GameType.TicTacToe -> {
                val p1 = Player.random()
                MultiPlayerMatch(
                    TicTacToeBoardRun(
                        initialTicTacToePositions(),
                        emptyList(),
                        Player.random(),
                        p1,
                        p1.other()
                        ),
                    Random.nextInt(from = 1, Int.MAX_VALUE),
                    player1,
                    null,
                    gameType
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
        return MultiPlayerMatch(
            board.play(move),
            id,
            player1,
            player2,
            gameType,

        )
    }

    /**
     * Function forfeit responsible to forfeit the game.
     * @param player the player that is forfeiting.
     * @return MultiPlayerGame the game after the forfeit.
     */
    fun forfeit(player: Int): MultiPlayerMatch {
        val playerType = getPlayerType(player)
        return MultiPlayerMatch(board.forfeit(playerType), id,player1, player2,gameType)
    }

    /**
     * Function getPlayerType responsible to get the player type.
     * @param userId the player.
     * @return Player the player type.
     */
    override fun getPlayerType(userId: Int): Player =
        if (userId == player1) board.player1 else board.player2
}

/**
 * Class "SinglePlayerGame" represents a single player game.
 * @param board the board of the game.
 * @param user the player.
 * @param difficulty the difficulty of the game.
 * @return Game the game that was created as a single player game.
 */
class SinglePlayerMatch(override val board: Board, override val id: Int, val user: UInt, difficulty: Difficulty):
    Match {
    override fun getPlayerType(userId: Int): Player = board.player1

}
