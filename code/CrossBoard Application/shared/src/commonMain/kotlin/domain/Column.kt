package domain

import kotlinx.serialization.Serializable

/**
 * Class "Column" represents the Column of the board.
 * @param symbol the symbol of the column.
 */
@Serializable
class Column private constructor(val symbol:Char) {
    //The index of the column.
    val index = symbol - 'a'

    companion object{
        operator fun invoke(c:Char) : Column {
            require(c in 'a' .. 'z'){"Column symbol must be between 'a' and 'z'"}
            return Column(c)
        }
    }
}