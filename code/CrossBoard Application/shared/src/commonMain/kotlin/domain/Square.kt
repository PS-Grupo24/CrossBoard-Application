package domain

import kotlinx.serialization.Serializable

/**
 * Data class "Square" represents a square on the board.
 * @param row the row of the square.
 * @param column the column of the square.
 */
@Serializable
data class Square(val row: Row, val column: Column){
    /**
     * Function "toString" that returns the string representation of the square.
     */
    override fun toString(): String {
        return "(${row.number}),(${column.symbol})"
    }
}