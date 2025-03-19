package domain

/**
 * Interface "Board" representing the game board and their properties.
 * @property positions List of all positions on the board.
 * @property moves List of all moves made on the board.
 * @property player1 Player 1.
 * @property player2 Player 2.
 * @property turn Player whose turn it is.
 * @property play Function to play a move on the board.
 * @property forfeit Function to forfeit the game board.
 * @property get Function to get the player at a specific square or to verify if it is occupied the square.
 */
interface Board {
    val positions: List<Position>
    val moves: List<Move>
    val player1: Player
    val player2: Player
    val turn: Player
    fun play(move: Move): Board
    fun forfeit(player: Player): Board
    fun get(square: Square): Player?
}

