package com.crossBoard.domain

/**
 * Data class "Square" represents a square on the board of the game.
 * @param row the row of the square.
 * @param column the column of the square.
 */
data class Square(val row: Row, val column: Column) {
    /**
     * Function "toString" that returns the string representation of the square.
     * @return String the string representation of the square.
     */
    override fun toString(): String = "${row.number}${column.symbol}"
}

/**
 * Function "toSquare" responsible to convert a String to a Square.
 * @param boardDim the dimension of the board.
 * @return Square the Square corresponding to the String.
 */
fun String.toSquare(boardDim: Int): Square {
    require(this.length == 2) {"The square input must have 2 characters."}
    require(this[0].isDigit()){"First Char is not a digit"}
    require(this[1].isLowerCase()){"Second Char isn't a lower case letter"}

    return Square(Row.invoke(boardDim - this[0].digitToInt(), boardDim), Column(this[1]))
}