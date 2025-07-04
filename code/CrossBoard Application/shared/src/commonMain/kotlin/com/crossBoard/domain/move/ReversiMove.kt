package com.crossBoard.domain.move

import com.crossBoard.domain.*
import com.crossBoard.domain.position.ReversiPosition

/**
 * Class `ReversiMove` represents a move in the game of Reversi.
 * @param player the player who made the move.
 * @param square the square where the move was made.
 */
data class ReversiMove(override val player: Player, val square: Square): Move {

    /**
     * Function `equals` compares two `ReversiMove` objects for equality.
     * @param other the other object to compare.
     * @return Boolean true if the objects are equal, false otherwise.
     */
    override fun equals(other: Any?): Boolean =
        other is ReversiMove && other.player == player && other.square == square

    /**
     * Function `hashCode` generates a hash code for the `ReversiMove` object.
     * @return Int the hash code of the object.
     */
    override fun hashCode(): Int = player.hashCode() + square.hashCode()
}


/**
 * Function `possibleMoves` calculates all possible moves for a player in a game of Reversi.
 * @param player the player for whom to calculate possible moves.
 * @param positions the current positions on the board, represented as a list of `ReversiPosition`.
 * @return a list of `Square` objects representing all possible moves for the player.
 */
fun possibleMoves(player: Player, positions: List<ReversiPosition>): List<Square> {
    val boardMap: Map<Square, Player> = positions.associate { it.square to it.player }

    val possibleMovesSet: MutableSet<Square> = mutableSetOf()

    val emptySquares = positions.filter { it.player == Player.EMPTY }.map { it.square }

    emptySquares.forEach outer@{ emptySquare ->
        Direction.entries.forEach inner@ { direction ->

            var currentScanSquare: Square? = emptySquare.adjust(direction)
            val flippedSquaresInLine = mutableListOf<Square>()

            while (currentScanSquare != null && boardMap[currentScanSquare] == player.other()) {
                flippedSquaresInLine.add(currentScanSquare)
                currentScanSquare = currentScanSquare.adjust(direction)
            }
            if (flippedSquaresInLine.isNotEmpty() && currentScanSquare != null && boardMap[currentScanSquare] == player) {
                possibleMovesSet.add(emptySquare)
                return@inner
            }
        }
    }
    return possibleMovesSet.toList()
}

/**
 * Function `turningPieces` calculates the new positions of pieces after a player makes a move in Reversi.
 * @param player the player who made the move.
 * @param finalSquare the square where the player made the move.
 * @param positions the current positions on the board, represented as a list of `ReversiPosition`.
 * @return a list of `ReversiPosition` objects representing the new state of the board after the move.
 */
fun turningPieces(player: Player, finalSquare: Square, positions: List<ReversiPosition>): List<ReversiPosition> {
    val boardMap: Map<Square, Player> = positions.associate { it.square to it.player }

    val squaresToFlip: MutableSet<Square> = mutableSetOf()

    Direction.entries.forEach { direction ->

        val potentialFlipsInDirection = mutableListOf<Square>()

        var currentScanSquare: Square? = finalSquare.adjust(direction)

        while (currentScanSquare != null) {
            val playerAtScanSquare = boardMap[currentScanSquare]

            when (playerAtScanSquare) {
                player -> {
                    if (potentialFlipsInDirection.isNotEmpty()) {
                        squaresToFlip.addAll(potentialFlipsInDirection)
                    }
                    currentScanSquare = null
                }
                Player.EMPTY -> {
                    currentScanSquare = null
                }
                null -> {
                    currentScanSquare = null
                }
                else -> {
                potentialFlipsInDirection.add(currentScanSquare)
                currentScanSquare = currentScanSquare.adjust(direction)
            }
            }
        }
    }
    val newPositions = positions.map { pos ->
        when {
            pos.square == finalSquare -> ReversiPosition(player, finalSquare)
            squaresToFlip.contains(pos.square) -> ReversiPosition(player, pos.square)
            else -> pos
        }
    }

    return newPositions
}