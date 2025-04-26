package crossBoard.domain


/**
 * class "Row" represents a row in the game.
 * @param index the index of the row.
 * @param boardDim the dimension of the board.
 */
class Row private constructor(val index:Int, boardDim:Int) {
    //The number of the row.
    val number = boardDim - index

    companion object{

        operator fun invoke(i:Int, boardDim: Int): Row {
            require(i in 0..<boardDim){"Index must be between 0 and Board Dimension."}
            return Row(i, boardDim)
        }
    }
}