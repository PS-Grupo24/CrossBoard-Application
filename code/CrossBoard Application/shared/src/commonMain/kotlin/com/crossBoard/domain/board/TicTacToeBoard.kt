package com.crossBoard.domain.board

import com.crossBoard.domain.Column
import com.crossBoard.domain.Player
import com.crossBoard.domain.Row
import com.crossBoard.domain.Square
import com.crossBoard.domain.move.Move
import com.crossBoard.domain.move.TicTacToeMove
import com.crossBoard.domain.position.TicPosition


/**
 * Abstract class "TicTacToeBoard" represents a TicTacToe board.
 * @param positions the list of positions in the board.
 * @param moves the list of moves made in the game.
 * @param turn the player who has the turn to play.
 * @param player1 the first player.
 * @param player2 the second player.
 * @return Board the TicTacToe board.
 */
abstract class TicTacToeBoard: Board {
    //Value representing the positions of the board.
    abstract override val positions: List<TicPosition>
    //Value representing the list of moves made in the board.
    abstract override val moves: List<TicTacToeMove>

    companion object{
        //The board dimension and the maximum moves.
        const val BOARD_DIM = 3
        const val MAX_MOVES = BOARD_DIM * BOARD_DIM
    }

    /**
     * Function get responsible to get the player at a specific square or to verify if it is occupied the square.
     * @param square the square to get the player.
     * @return Player the player at the square.
     */
    override fun get(square: Square): Player? =
        positions.find { it.square.row.number == square.row.number && it.square.column.symbol == square.column.symbol }?.player

    /**
     * Function "equals" responsible to compare two TicTacToeBoard objects.
     * @param other the other object to compare.
     * @return Boolean true if the objects are equal, false otherwise.
     */
    override fun equals(other: Any?) =
        other is TicTacToeBoard && other.positions == positions

    /**
     * Function "hashCode" responsible to generate a hash code for the TicTacToeBoard object.
     * @return Int the hash code of the object.
     */
    override fun hashCode(): Int = positions.hashCode()
}

/**
 * Data class "TicTacToeBoardRun" represents a Tic Tac Toe board in a running state.
 * @param positions the list of positions in the board.
 * @param moves the list of moves made in the board.
 * @param turn the player who has the turn to play.
 * @param player1 the first player.
 * @param player2 the second player.
 * @return TicTacToeBoard the TicTacToe board in a running state with the information.
 * @return BoardRun the TicTacToe board in a running state with the information.
 */
class TicTacToeBoardRun(
    override val positions: List<TicPosition>,
    override val moves: List<TicTacToeMove>,
    override val turn: Player,
    override val player1: Player,
    override val player2: Player,
): TicTacToeBoard(), BoardRun {

    /**
     * Function "play" responsible to play a move on the board.
     * @param move The move to be done in this play.
     * @return `Board` - The board after the move was played.
     */
    override fun play(move: Move): Board {
        require(move is TicTacToeMove) {"Wrong move type"}
        require(move.player == turn) {"Not this player's turn to play!"}
        require(get(move.square) == Player.EMPTY) {"This position is not empty!"}
        val newPositions = positions.map {
            if (it.square.row.number == move.square.row.number && it.square.column.symbol == move.square.column.symbol)
                TicPosition(move.player, move.square)
            else it
        }

        return when {
            verifyWinner(newPositions, move) ->
                TicTacToeBoardWin(move.player, newPositions, moves + move, turn.other(), player1, player2)
            moves.size == MAX_MOVES - 1 ->
                TicTacToeBoardDraw(newPositions, moves + move, turn.other(), player1, player2)
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
    private fun verifyWinner(positions: List<TicPosition>, move: Move): Boolean {
        require(move is TicTacToeMove) {"Wrong type of move!"}
        val playerPositions = positions.filter { it.player == move.player}
        return playerPositions.count{it.square.column.symbol == move.square.column.symbol} == BOARD_DIM
                || playerPositions.count{it.square.row.number == move.square.row.number} == BOARD_DIM
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
 * @return BoardWin the Tic Tac Toe board in a win state with the information.
 */
class TicTacToeBoardWin(
    override val winner: Player,
    override val positions: List<TicPosition>,
    override val moves: List<TicTacToeMove>,
    override val turn: Player,
    override val player1: Player,
    override val player2: Player,
): TicTacToeBoard(), BoardWin {

    /**
     * Function play responsible to play a move on the board.
     * @param move the move to be done in this play.
     * @return Board the board after the move was played.
     */
    override fun play(move: Move): Board = throw IllegalStateException("The player $winner already won the game.")

    /**
     * Function forfeit responsible to forfeit the game board.
     * @param player the player that is forfeiting.
     * @return Board the game after the player forfeited.
     */
    override fun forfeit(player: Player): Board = throw IllegalStateException("Can not forfeit an already finished game!")
}

/**
 * Data class "TicTacToeBoardDraw" represents a Tic Tac Toe board in a draw state.
 * @param positions the list of positions in the board.
 * @param moves the list of moves made in the game.
 * @param turn the player who has the turn to play.
 * @param player1 the first player.
 * @param player2 the second player.
 * @return TicTacToeBoard the Tic Tac Toe board in a draw state with the information.
 * @return BoardDraw the Tic Tac Toe board in a draw state with the information.
 */
class TicTacToeBoardDraw(
    override val positions: List<TicPosition>,
    override val moves: List<TicTacToeMove>,
    override val turn: Player,
    override val player1: Player,
    override val player2: Player,
) : TicTacToeBoard(), BoardDraw {

    /**
     * Function play responsible to play a move on the board.
     * @param move the move to be done in this play.
     * @return Board the board after the move was played.
     */
    override fun play(move: Move): Board = throw IllegalStateException("The game has already ended on a draw.")

    /**
     * Function forfeit responsible to forfeit the game board.
     * @param player the player that is forfeiting.
     * @return Board the game after the player forfeited.
     */
    override fun forfeit(player: Player): Board = throw IllegalStateException("Can not forfeit an already finished game!")
}

/**
 * Function initialTicTacToePositions responsible to create the initial positions of the Tic Tac Toe board.
 * @return List<TicPosition> the list of initial positions.
 */
fun initialTicTacToePositions():List<TicPosition> {
    val positions = mutableListOf<TicPosition>()
    repeat(TicTacToeBoard.BOARD_DIM){ line ->
        repeat(TicTacToeBoard.BOARD_DIM){ col ->
                positions.add(
                    TicPosition(
                        Player.EMPTY,
                        Square(
                            Row.Companion.invoke(
                                line,
                                TicTacToeBoard.BOARD_DIM
                            ),
                            Column.Companion('a' + col)
                        )
                    )
                )
        }
    }
    return positions
}