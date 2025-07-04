package com.crossBoard.domain

/**
 * Enum class "Direction" represents the possible directions in which a move can be made on the board of Reversi.
 * @property difRow the difference in the row index when moving in this direction.
 * @property difCol the difference in the column index when moving in this direction.
 */
enum class Direction(val difRow: Int, val difCol: Int) {
    UP(-1, 0), DOWN(1, 0), LEFT(0, -1), RIGHT(0, 1),
    UP_LEFT(-1, -1), UP_RIGHT(-1, 1), DOWN_LEFT(1, -1), DOWN_RIGHT(1, 1)
}