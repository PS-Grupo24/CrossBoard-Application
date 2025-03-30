package domain

/**
 * Enum class "GameType" represents the type of the game.
 * @property TicTacToe the Tic Tac Toe game.
 */
enum class GameType {
    TicTacToe
}

fun String.toGameType() : GameType? =
    when {
        this == "tic" -> GameType.TicTacToe
        else -> null
    }
