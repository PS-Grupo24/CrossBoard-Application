package org.example.project.model


interface Game{
    val board: Board;
}

enum class GameType(type: String){
    TicTacToe("tic")
}

class MultiPlayerGame(override val board: Board, val player1: User, val player2:User) : Game{
    companion object{
        fun startGame(player1: User, player2: User, gameType: GameType): Game = when(gameType){
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
    fun play(player:User, row: Int, column: Char): MultiPlayerGame{
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

    fun forfeit(player: User): MultiPlayerGame{
        val p = getPlayerType(player)
        return MultiPlayerGame(board.forfeit(p), player1, player2)
    }

    private fun getPlayerType(player: User): Player =
        if (player == player1) board.player1 else board.player2
}

class SinglePlayerGame(override val board: Board, val player2: User, difficulty: Difficulty) : Game
