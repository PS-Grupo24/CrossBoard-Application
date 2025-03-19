package model

import domain.Board
import domain.Difficulty
import domain.Move
import domain.Player
import domain.TicTacToeBoardRun
import domain.initialTicTacToePositions
import kotlinx.serialization.Serializable
import kotlin.random.Random
import kotlin.random.nextUInt

/**
 * Interface "Game" represents a game.
 * @property board the board of the game.
 */
interface Match {
    val id: UInt
    val board: Board;
}

/**
 * Enum class "GameType" represents the type of the game.
 * @param type the type of the game.
 * @property TicTacToe the Tic Tac Toe game.
 */
enum class GameType(type: String) {
    TicTacToe("tic")
}

/**
 * Class "MultiPlayerGame" represents a multiplayer game.
 * @param board the board of the game.
 * @param player1 the first player.
 * @param player2 the second player.
 * @return Game the game that was created as a multiplayer game.
 */
@Serializable
class MultiPlayerMatch(
    override val board: Board,
    override val id: UInt,
    val player1: UInt,
    var player2: UInt? = null,
    val gameType: GameType
): Match {
    companion object{
        /**
         * Function startGame responsible to start a game.
         * @param player1 the first player.
         * @param gameType the type of the game.
         * @return Game the game that was created.
         */
        fun startGame(player1: UInt, gameType: GameType): MultiPlayerMatch = when(gameType){
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
                    Random.nextUInt(),
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
            gameType
        )
    }

    /**
     * Function forfeit responsible to forfeit the game.
     * @param player the player that is forfeiting.
     * @return MultiPlayerGame the game after the forfeit.
     */
    fun forfeit(player: UInt): MultiPlayerMatch {
        val playerType = getPlayerType(player)
        return MultiPlayerMatch(board.forfeit(playerType), id,player1, player2,gameType)
    }

    /**
     * Function getPlayerType responsible to get the player type.
     * @param player the player.
     * @return Player the player type.
     */
    private fun getPlayerType(player: UInt): Player =
        if (player == player1) board.player1 else board.player2
}

/**
 * Class "SinglePlayerGame" represents a single player game.
 * @param board the board of the game.
 * @param user the player.
 * @param difficulty the difficulty of the game.
 * @return Game the game that was created as a single player game.
 */
class SinglePlayerMatch(override val board: Board, override val id: UInt, val user: UInt, difficulty: Difficulty): Match
