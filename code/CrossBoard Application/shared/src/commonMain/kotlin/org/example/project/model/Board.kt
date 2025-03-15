package org.example.project.model

const val BOARD_DIM = 3
const val MAX_MOVES = BOARD_DIM * BOARD_DIM
sealed class Board(
    val positions: List<Position>,
    val moves:List<Move>,
    val turn:Player
){
    abstract fun play(player: Player, square: Square):Board
    abstract fun forfeit(player: Player):Board
    fun get(square: Square) = positions.find { it.square == square }?.player
}

class BoardRun(
    positions: List<Position>,
    moves: List<Move>,
    turn: Player,
) : Board(positions, moves, turn) {
    override fun play(player: Player, square: Square): Board {
        require(player == turn){"Not this player's turn to play!"}
        require(positions.find { it.square == square }?.player == Player.EMPTY)
            {"This position is not empty!"}
        val move = Move(player, square)
        val newPositions = positions.map { if (it.square == square) Position(player, square) else it }
        return when {
            verifyWinner(newPositions, move) -> BoardWin(player, newPositions, moves + move, turn.other())
            moves.size == MAX_MOVES - 1 -> BoardDraw(newPositions, moves + move, turn.other())
            else -> BoardRun(newPositions, moves + move, turn.other())
        }
    }

    override fun forfeit(player: Player): Board = BoardWin(player.other(), positions, moves, turn)

    private fun verifyWinner(positions: List<Position>, move: Move): Boolean {
        val playerPositions = positions.filter { it.player == move.player}
        return playerPositions.count{it.square.column == move.square.column} == BOARD_DIM
                || playerPositions.count{it.square.row == move.square.row} == BOARD_DIM
                || playerPositions.count{it.square.row.index == it.square.column.index} == BOARD_DIM
                || playerPositions.count{it.square.row.index == BOARD_DIM - it.square.column.index - 1} == BOARD_DIM
    }
}

class BoardWin(
    val winner: Player,
    positions: List<Position>,
    moves: List<Move>,
    turn: Player
) : Board(positions, moves, turn){
    override fun play(player: Player, square: Square): Board {
        throw IllegalStateException("The player $winner already won the game.")
    }

    override fun forfeit(player: Player): Board {
        throw IllegalStateException("Can not forfeit and already finished game!")
    }
}

class BoardDraw(
    positions: List<Position>,
    moves: List<Move>,
    turn: Player
) : Board(positions, moves, turn){

    override fun play(player: Player, square: Square): Board {
        throw IllegalStateException("The game has already ended on a draw.")
    }

    override fun forfeit(player: Player): Board {
        throw IllegalStateException("Can not forfeit and already finished game!")
    }
}
