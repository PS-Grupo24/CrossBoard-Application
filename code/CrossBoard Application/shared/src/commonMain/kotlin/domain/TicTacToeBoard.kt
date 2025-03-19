package domain

/**
 * Data class "TicTacToeBoard" represents a Tic Tac Toe board.
 * @param positions the list of positions in the board.
 * @param moves the list of moves made in the game.
 * @param turn the player who has the turn to play.
 * @param player1 the first player.
 * @param player2 the second player.
 * @return Board the Tic Tac Toe board.
 */
sealed class TicTacToeBoard(
    override val positions: List<Position>,
    override val moves:List<Move>,
    override val turn: Player,
    override val player1: Player,
    override val player2: Player
): Board {
    companion object{
        //The board dimension and the maximum moves.
        const val BOARD_DIM = 3
        const val MAX_MOVES = BOARD_DIM * BOARD_DIM
    }

    /**
     * Function play responsible to play a move on the board.
     * @param move the move to be done in the play.
     * @return Board the game after the move was played.
     */
    abstract override fun play(move: Move): Board

    /**
     * Function forfeit responsible to forfeit the game board.
     * @param player the player that is forfeiting.
     * @return Board the game after the player forfeited.
     */
    abstract override fun forfeit(player: Player): Board

    /**
     * Function get responsible to get the player at a specific square or to verify if it is occupied the square.
     * @param square the square to get the player.
     * @return Player the player at the square.
     */
    override fun get(square: Square) = positions.find { it.square == square }?.player
}

/**
 * Data class "TicTacToeBoardRun" represents a Tic Tac Toe board in a running state.
 * @param positions the list of positions in the board.
 * @param moves the list of moves made in the game.
 * @param turn the player who has the turn to play.
 * @param player1 the first player.
 * @param player2 the second player.
 * @return TicTacToeBoard the Tic Tac Toe board in a running state with the information.
 */
class TicTacToeBoardRun(
    positions: List<Position>,
    moves: List<Move>,
    turn: Player,
    player1: Player,
    player2: Player
) : TicTacToeBoard(positions, moves, turn, player1, player2), BoardRun {
    /**
     * Function play responsible to play a move on the board.
     * @param move the move to be done in this play.
     * @return Board the board after the move was played.
     */
    override fun play(move: Move): Board {
        require(move is TicTacToeMove){"Wrong move format"}
        require(move.player == turn){"Not this player's turn to play!"}
        require(get(move.square) == Player.EMPTY) {"This position is not empty!"}
        val newPositions = positions.map { if (it.square == move.square) Position(move.player, move.square) else it }

        return when {
            verifyWinner(newPositions, move) ->
                TicTacToeBoardWin(move.player, newPositions, moves + move, turn.other(), player1, player2)
            moves.size == MAX_MOVES - 1 ->
                TicTacToeBoardDraw(newPositions, moves + move, turn.other(),player1, player2)
            else ->
                TicTacToeBoardRun(newPositions, moves + move, turn.other(), player1, player2)
        }
    }

    /**
     * Function forfeit responsible to forfeit the game board.
     * @param player the player that is forfeiting.
     * @return Board the game after the player forfeited.
     */
    override fun forfeit(player: Player): Board = TicTacToeBoardWin(player.other(), positions, moves, turn, player1, player2)

    /**
     * Function verifyWinner responsible to verify if there is a winner in the game.
     * @param positions the list of positions in the board.
     * @param move the move that was made.
     * @return Boolean true if there is a winner, false otherwise.
     */
    private fun verifyWinner(positions: List<Position>, move: Move): Boolean {
        require(move is TicTacToeMove){"Wrong type of move!"}
        val playerPositions = positions.filter { it.player == move.player}
        return playerPositions.count{it.square.column == move.square.column} == BOARD_DIM
                || playerPositions.count{it.square.row == move.square.row} == BOARD_DIM
                || playerPositions.count{it.square.row.index == it.square.column.index} == BOARD_DIM
                || playerPositions.count{it.square.row.index == BOARD_DIM - it.square.column.index - 1} == BOARD_DIM
    }
}

/**
 * Data class "TicTacToeBoardWin" represents a Tic Tac Toe board in a win state.
 * @param winner the player who won the game.
 * @param positions the list of positions in the board.
 * @param moves the list of moves made in the game.
 * @param turn the player who has the turn to play.
 * @param player1 the first player.
 * @param player2 the second player.
 * @return TicTacToeBoard the Tic Tac Toe board in a win state with the information.
 */
class TicTacToeBoardWin(
    val winner: Player,
    positions: List<Position>,
    moves: List<Move>,
    turn: Player,
    player1: Player,
    player2: Player
) : TicTacToeBoard(positions, moves, turn, player1, player2), BoardWin{
    /**
     * Function play responsible to play a move on the board.
     * @param player the player that is playing.
     * @param row the row of the move.
     * @param column the column of the move.
     * @return Board the board after the move was played.
     */
    override fun play(move: Move): Board {
        throw IllegalStateException("The player $winner already won the game.")
    }

    /**
     * Function forfeit responsible to forfeit the game board.
     * @param player the player that is forfeiting.
     * @return Board the game after the player forfeited.
     */
    override fun forfeit(player: Player): Board {
        throw IllegalStateException("Can not forfeit an already finished game!")
    }
}

/**
 * Data class "TicTacToeBoardDraw" represents a Tic Tac Toe board in a draw state.
 * @param positions the list of positions in the board.
 * @param moves the list of moves made in the game.
 * @param turn the player who has the turn to play.
 * @param player1 the first player.
 * @param player2 the second player.
 * @return TicTacToeBoard the Tic Tac Toe board in a draw state with the information.
 */
class TicTacToeBoardDraw(
    positions: List<Position>,
    moves: List<Move>,
    turn: Player,
    player1: Player,
    player2: Player
) : TicTacToeBoard(positions, moves, turn, player1, player2), BoardDraw{

    /**
     * Function play responsible to play a move on the board.
     * @param player the player that is playing.
     * @param row the row of the move.
     * @param column the column of the move.
     * @return Board the board after the move was played.
     */
    override fun play(move: Move): Board {
        throw IllegalStateException("The game has already ended on a draw.")
    }

    /**
     * Function forfeit responsible to forfeit the game board.
     * @param player the player that is forfeiting.
     * @return Board the game after the player forfeited.
     */
    override fun forfeit(player: Player): Board {
        throw IllegalStateException("Can not forfeit an already finished game!")
    }
}

/**
 * Function initialTicTacToePositions responsible to create the initial positions of the Tic Tac Toe board.
 * @return List<Position> the list of initial positions.
 */
fun initialTicTacToePositions():List<Position> =
    List(TicTacToeBoard.BOARD_DIM * TicTacToeBoard.BOARD_DIM){
        Position(
            Player.EMPTY,
            Square(
                Row.invoke(it, TicTacToeBoard.BOARD_DIM),
                Column('a' + it)
            )
        )
    }
