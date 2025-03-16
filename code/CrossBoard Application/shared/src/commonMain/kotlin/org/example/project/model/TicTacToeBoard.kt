package org.example.project.model

sealed class TicTacToeBoard(
    override val positions: List<Position>,
    override val moves:List<Move>,
    override val turn:Player
) : Board{
    companion object{
        const val BOARD_DIM = 3
        const val MAX_MOVES = BOARD_DIM * BOARD_DIM
    }
    abstract override fun play(player: Player, square: Square):Board
    abstract override fun forfeit(player: Player):Board
    override fun get(square: Square) = positions.find { it.square == square }?.player
}

class TicTacToeBoardRun(
    positions: List<Position>,
    moves: List<Move>,
    turn: Player,
) : TicTacToeBoard(positions, moves, turn) {
    override fun play(player: Player, square: Square): Board {
        require(player == turn){"Not this player's turn to play!"}
        require(get(square) == Player.EMPTY) {"This position is not empty!"}

        val move = Move(player, square)
        val newPositions = positions.map { if (it.square == square) Position(player, square) else it }

        return when {
            verifyWinner(newPositions, move) -> TicTacToeBoardWin(player, newPositions, moves + move, turn.other())
            moves.size == MAX_MOVES - 1 -> TicTacToeBoardDraw(newPositions, moves + move, turn.other())
            else -> TicTacToeBoardRun(newPositions, moves + move, turn.other())
        }
    }

    override fun forfeit(player: Player): Board = TicTacToeBoardWin(player.other(), positions, moves, turn)

    private fun verifyWinner(positions: List<Position>, move: Move): Boolean {
        val playerPositions = positions.filter { it.player == move.player}
        return playerPositions.count{it.square.column == move.square.column} == BOARD_DIM
                || playerPositions.count{it.square.row == move.square.row} == BOARD_DIM
                || playerPositions.count{it.square.row.index == it.square.column.index} == BOARD_DIM
                || playerPositions.count{it.square.row.index == BOARD_DIM - it.square.column.index - 1} == BOARD_DIM
    }
}

class TicTacToeBoardWin(
    val winner: Player,
    positions: List<Position>,
    moves: List<Move>,
    turn: Player
) : TicTacToeBoard(positions, moves, turn){
    override fun play(player: Player, square: Square): Board {
        throw IllegalStateException("The player $winner already won the game.")
    }

    override fun forfeit(player: Player): Board {
        throw IllegalStateException("Can not forfeit an already finished game!")
    }
}

class TicTacToeBoardDraw(
    positions: List<Position>,
    moves: List<Move>,
    turn: Player
) : TicTacToeBoard(positions, moves, turn){

    override fun play(player: Player, square: Square): Board {
        throw IllegalStateException("The game has already ended on a draw.")
    }

    override fun forfeit(player: Player): Board {
        throw IllegalStateException("Can not forfeit and already finished game!")
    }
}

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
