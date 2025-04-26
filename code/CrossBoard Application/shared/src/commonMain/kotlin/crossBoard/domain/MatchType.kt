package crossBoard.domain

/**
 * Enum class "GameType" represents the type of the game.
 * @property TicTacToe the Tic Tac Toe game.
 */

const val TIC_VALUE = "tic"

enum class MatchType(val value: String) {
    TicTacToe(TIC_VALUE);

    override fun toString(): String = value
}
fun String.toMatchType() : MatchType =
    when(this) {
        TIC_VALUE -> MatchType.TicTacToe
        else -> throw IllegalArgumentException("Wrong MatchType $this")
    }