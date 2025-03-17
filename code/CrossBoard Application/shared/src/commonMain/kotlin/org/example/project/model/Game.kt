package org.example.project.model

/**
 * Interface "Game" represents a game.
 * @property board the board of the game.
 */
interface Game {
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
class MultiPlayerGame(override val board: Board, val player1: User, val player2:User):Game {
    companion object{
        /**
         * Function startGame responsible to start a game.
         * @param player1 the first player.
         * @param player2 the second player.
         * @param gameType the type of the game.
         * @return Game the game that was created.
         */
        fun startGame(player1: User, player2: User, gameType: GameType):Game = when(gameType){
            GameType.TicTacToe -> {
                val p1 = Player.random()
                MultiPlayerGame(
                    TicTacToeBoardRun(
                        initialTicTacToePositions(),
                        emptyList(),
                        Player.random(),
                        p1,
                        p1.other()
                        ),
                    player1,
                    player2
                )
            }
        }
    }

    /**
     * Function play responsible to play a move on the board.
     * @param player the player that is playing.
     * @param row the row of the move.
     * @param column the column of the move.
     * @return MultiPlayerGame the game after the move was played.
     */
    fun play(player:User, row: Int, column: Char):MultiPlayerGame {
        val p = getPlayerType(player)
        return MultiPlayerGame(
            board.play(
                p,
                row,
                column
            ),
            player1,
            player2
        )
    }

    /**
     * Function forfeit responsible to forfeit the game.
     * @param player the player that is forfeiting.
     * @return MultiPlayerGame the game after the forfeit.
     */
    fun forfeit(player: User):MultiPlayerGame {
        val playerType = getPlayerType(player)
        return MultiPlayerGame(board.forfeit(playerType), player1, player2)
    }

    /**
     * Function getPlayerType responsible to get the player type.
     * @param player the player.
     * @return Player the player type.
     */
    private fun getPlayerType(player: User):Player =
        if (player == player1) board.player1 else board.player2
}

/**
 * Class "SinglePlayerGame" represents a single player game.
 * @param board the board of the game.
 * @param player the player.
 * @param difficulty the difficulty of the game.
 * @return Game the game that was created as a single player game.
 */
class SinglePlayerGame(override val board: Board, val player: User, difficulty: Difficulty):Game
