package com.crossBoard.domain.board

import com.crossBoard.domain.Column
import com.crossBoard.domain.Player
import com.crossBoard.domain.Row
import com.crossBoard.domain.Square
import com.crossBoard.domain.move.Move
import com.crossBoard.domain.move.ReversiMove
import com.crossBoard.domain.move.possibleMoves
import com.crossBoard.domain.move.turningPieces
import com.crossBoard.domain.position.ReversiPosition

/**
 * Abstract class "ReversiBoard" representing a Reversi board.
 * It extends the Board interface and provides common functionality for Reversi boards.
 */
abstract class ReversiBoard(): Board {
    //Values representing the moves made in the reversi board and the positions of the board.
    abstract override val positions: List<ReversiPosition>
    abstract override val moves: List<ReversiMove>

    companion object {
        //Values representing the dimensions of the Reversi board and the maximum number of moves.
        const val BOARD_DIM = 8
        const val MAX_MOVES = BOARD_DIM * BOARD_DIM
    }

    /**
     * Function to "get" the current player on the square.
     * @param square The square to check for the player.
     * @return The player on the square, or null if the square is empty.
     */
    override fun get(square: Square): Player? =
        positions.find {it.square.row.number == square.row.number && it.square.column.symbol == square.column.symbol }?.player

    /**
     * Function "equals" to compare two ReversiBoard objects.
     * @param other The other object to compare with the Reversi Board.
     * @return Boolean true if the other object is a ReversiBoard and has the same positions, false otherwise.
     */
    override fun equals(other: Any?) = other is ReversiBoard && positions == other.positions

    /**
     * Function "hashCode" to generate a hash code for the ReversiBoard.
     * @return Int the hash code of the positions in the ReversiBoard.
     */
    override fun hashCode(): Int = positions.hashCode()

    /**
     * Function "skip" responsible for skipping the turn of the player when he can´t make any moves.
     * @param player the player who wants to skip his turn.
     * @return Board the new board after skipping the turn.
     */
    abstract fun skip(player: Player): Board
}

/**
 * Class "ReversiBoardRun" representing a running Reversi board.
 * It extends the ReversiBoard and implements the BoardRun interface.
 * @param positions the current positions on the board.
 * @param moves the list of moves made in the board.
 * @param turn the player whose turn it is to play.
 * @param player1 the first player in the game.
 * @param player2 the second player in the game.
 */
class ReversiBoardRun(
    override val positions: List<ReversiPosition>,
    override val moves: List<ReversiMove>,
    override val turn: Player,
    override val player1: Player,
    override val player2: Player,
): ReversiBoard(), BoardRun {

    /**
     * Function "play" responsible for playing a move in the Reversi board.
     * @param move the move to be played.
     * @return Board the new board after the move is played.
     */
    override fun play(move: Move): Board {
        require(move is ReversiMove) {"Wrong move type"}
        require(move.player == turn) {"Not this player's turn to play."}
        require(get(move.square) == Player.EMPTY) {"This positions is not empty."}
        return this.playingBoard(move)
    }

    /**
     * Function "skip" responsible for skipping the turn of the player when he can´t make any moves.
     * @param player the player who wants to skip his turn.
     * @return Board the new board after skipping the turn.
     */
    override fun skip(player: Player): Board {
        require(turn == player){"Can't skip when it is not your turn."}
        require(possibleMoves(player, positions).isEmpty()){"Player $player can not skip, there are possible moves."}
        return ReversiBoardRun(
            positions,
            moves,
            turn.other(),
            player1,
            player2
        )
    }

    /**
     * Function "forfeit" responsible for forfeiting the game board.
     * @param player the player that is forfeiting.
     * @return Board the game after the player forfeited.
     */
    override fun forfeit(player: Player): Board = ReversiBoardWin(player.other(), positions, moves, turn, player1, player2)

    /**
     * Function "playingBoard" responsible for playing a move on the board.
     * @param move the move to be done in this play.
     * @return Board the board after the move was played.
     */
    private fun playingBoard(move: ReversiMove): Board {
        val validMoves = possibleMoves(turn, positions)
        require(validMoves.contains(move.square)) { "Invalid Move" }
        val newPositions = turningPieces(turn, move.square, positions)
        val newMoves = moves + move
        val newBoard = ReversiBoardRun(
            newPositions,
            newMoves,
            turn.other(),
            player1,
            player2
        )

        if (newBoard.moves.size == MAX_MOVES - 4) return finalReversiBoard(newPositions, newMoves)  //If max moves are reached, return final board.
        val newBoardTurnPossibleMoves = possibleMoves(newBoard.turn, newBoard.positions)
        if (newBoardTurnPossibleMoves.isNotEmpty()) return newBoard                                 //If new turn has playable moves, return new board.

        //If new turn has no playable moves, check if skipping is possible.
        val possibleMovesIfNewBoardSkipped = possibleMoves(newBoard.skip(newBoard.turn).turn, newBoard.positions)
        if (possibleMovesIfNewBoardSkipped.isNotEmpty() ) return newBoard.skip(newBoard.turn)

        //If skipping is not possible, return final board.
        return finalReversiBoard(newPositions, newMoves)
    }

    /**
     * Function "finalReversiBoard" responsible for creating and determine the final board after the game is finished.
     * @param newPositions the final positions of the board.
     * @param newMoves the final moves made in the game.
     * @return Board the final board after the game is finished.
     */
    private fun finalReversiBoard(newPositions: List<ReversiPosition>, newMoves: List<ReversiMove>): Board {
        val bCount = newPositions.count{it.player == Player.BLACK }
        val wCount = newPositions.count{it.player == Player.WHITE }
        val dif = bCount - wCount
        val board = when {
            (dif > 0) -> ReversiBoardWin(Player.BLACK, newPositions, newMoves, turn.other(), player1, player2)
            (dif < 0) -> ReversiBoardWin(Player.WHITE, newPositions, newMoves, turn.other(), player1, player2)
            else -> ReversiBoardDraw(newPositions, newMoves, turn.other(), player1, player2)
        }
        return board
    }
}

/**
 * Class "ReversiBoardWin" representing a finished Reversi board with a winner.
 * It extends the ReversiBoard and implements the BoardWin interface.
 * @param winner the player who won the game.
 * @param positions the final positions on the board.
 * @param moves the list of moves made in the game.
 * @param turn the player whose turn it was when the game ended.
 * @param player1 the first player in the game.
 * @param player2 the second player in the game.
 */
class ReversiBoardWin(
    override val winner: Player,
    override val positions: List<ReversiPosition>,
    override val moves: List<ReversiMove>,
    override val turn: Player,
    override val player1: Player,
    override val player2: Player
): ReversiBoard(), BoardWin {

    /**
     * Function "play" responsible for playing a move on the board.
     * @param move the move to be played.
     * @return Board the board after the move was played.
     * @throws IllegalStateException if the game has already ended with a winner.
     */
    override fun play(move: Move): Board = throw IllegalStateException("The player $winner already won the game.")

    /**
     * Function "forfeit" responsible for forfeiting the game board.
     * @param player the player that is forfeiting.
     * @return Board the game after the player forfeited.
     * @throws IllegalStateException if the game has already ended with a winner.
     */
    override fun forfeit(player: Player): Board = throw IllegalStateException("Can not forfeit an already finished game.")

    /**
     * Function "skip" responsible for skipping the turn of the player when he can´t make any moves.
     * @param player the player who wants to skip his turn.
     * @return Board the new board after skipping the turn.
     * @throws IllegalStateException if the game has already ended with a winner.
     */
    override fun skip(player: Player): Board = throw IllegalStateException("Can not skip an already finished game.")
}

/**
 * Class "ReversiBoardDraw" representing a finished Reversi board with a draw.
 * It extends the ReversiBoard and implements the BoardDraw interface.
 * @param positions the final positions on the board.
 * @param moves the list of moves made in the game.
 * @param turn the player whose turn it was when the game ended.
 * @param player1 the first player in the game.
 * @param player2 the second player in the game.
 * @return ReversiBoard the Reversi board in a draw state with the information.
 */
class ReversiBoardDraw(
    override val positions: List<ReversiPosition>,
    override val moves: List<ReversiMove>,
    override val turn: Player,
    override val player1: Player,
    override val player2: Player
): ReversiBoard(), BoardDraw {

    /**
     * Function "play" responsible for playing a move on the board.
     * @param move the move to be played.
     * @return Board the board after the move was played.
     * @throws IllegalStateException if the game has already ended in a draw.
     */
    override fun play(move: Move): Board = throw IllegalStateException("The game has already ended on a draw.")

    /**
     * Function "skip" responsible for skipping the turn of the player when he can´t make any moves.
     * @param player the player who wants to skip his turn.
     * @return Board the new board after skipping the turn.
     * @throws IllegalStateException if the game has already ended in a draw.
     */
    override fun skip(player: Player): Board = throw IllegalStateException("Can not skip an already finished game.")

    /**
     * Function "forfeit" responsible for forfeiting the game board.
     * @param player the player that is forfeiting.
     * @return Board the game after the player forfeited.
     * @throws IllegalStateException if the game has already ended in a draw.
     */
    override fun forfeit(player: Player): Board = throw IllegalStateException("Can not forfeit an already finished game!")
}

/**
 * Function "initialReversiPositions" responsible for creating the initial positions of a Reversi board.
 * @return List<ReversiPosition> the list of initial positions in the Reversi board.
 */
fun initialReversiPositions(): List<ReversiPosition> {
    val positions = mutableListOf<ReversiPosition>()
    repeat(ReversiBoard.BOARD_DIM){ line ->
        repeat(ReversiBoard.BOARD_DIM){ col ->
            val square = Square(Row(line, ReversiBoard.BOARD_DIM), Column('a' + col))
            val player = when {
                (line == ReversiBoard.BOARD_DIM / 2 - 1 && col == ReversiBoard.BOARD_DIM / 2 - 1) ||
                        (line == ReversiBoard.BOARD_DIM / 2 && col == ReversiBoard.BOARD_DIM / 2) -> Player.WHITE
                (line == ReversiBoard.BOARD_DIM / 2 - 1 && col == ReversiBoard.BOARD_DIM / 2) ||
                        (line == ReversiBoard.BOARD_DIM / 2 && col == ReversiBoard.BOARD_DIM / 2 - 1) -> Player.BLACK
                else -> Player.EMPTY
            }
            positions.add(ReversiPosition(player, square))
        }
    }
    return positions
}


