package domain


/**
 * Data class "Square" represents a square on the board.
 * @param row the row of the square.
 * @param column the column of the square.
 */
data class Square(val row: Row, val column: Column){
    /**
     * Function "toString" that returns the string representation of the square.
     */
    override fun toString(): String {
        return "${row.number}${column.symbol}"
    }
}

fun String.toSquare(boardDim: Int) : Square{
    require(this.length == 2) {"This move input must contain a maximum size 2"}
    require(this[0].isDigit()){"First Char is not a digit"}
    require(this[1].isLowerCase()){"Second Char isn't a lower case letter"}

    return Square(Row.invoke(boardDim - this[0].digitToInt(), boardDim), Column(this[1]))
}