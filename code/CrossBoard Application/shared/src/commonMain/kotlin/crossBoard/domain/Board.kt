package crossBoard.domain

/**
 * Sealed Interface "Board" representing the game board and their properties.
 * @property positions List of all positions on the board.
 * @property moves List of all moves made on the board.
 * @property player1 Player 1.
 * @property player2 Player 2.
 * @property turn Player whose turn it is.
 * @property play Function to play a move on the board.
 * @property forfeit Function to forfeit the game board.
 * @property get Function to get the player at a specific square or to verify if it is occupied the square.
 */
sealed interface Board {
    val positions: List<Position>
    val moves: List<Move>
    val player1: Player
    val player2: Player
    val turn: Player
    fun play(move: Move): Board
    fun forfeit(player: Player): Board
    fun get(square: Square): Player?
}

/**
 * Sealed Interface "BoardRun" representing the game board in a running state.
 * @property positions List of all positions on the board.
 * @property moves List of all moves made on the board.
 * @property player1 Player 1.
 * @property player2 Player 2.
 * @property turn Player whose turn it is.
 * @property play Function to play a move on the board.
 * @property forfeit Function to forfeit the game board.
 * @property get Function to get the player at a specific square or to verify if it is occupied the square.
 */
sealed interface BoardRun : Board

/**
 * Sealed Interface "BoardWin" representing the game board in a winning state.
 * @property positions List of all positions on the board.
 * @property moves List of all moves made on the board.
 * @property player1 Player 1.
 * @property player2 Player 2.
 * @property turn Player whose turn it is.
 * @property winner Player who won the game.
 * @property play Function to play a move on the board.
 * @property forfeit Function to forfeit the game board.
 * @property get Function to get the player at a specific square or to verify if it is occupied the square.
 */
sealed interface BoardWin : Board {
    val winner: Player
}

/**
 * Sealed Interface "BoardDraw" representing the game board in a draw state.
 * @property positions List of all positions on the board.
 * @property moves List of all moves made on the board.
 * @property player1 Player 1.
 * @property player2 Player 2.
 * @property turn Player whose turn it is.
 * @property play Function to play a move on the board.
 * @property forfeit Function to forfeit the game board.
 * @property get Function to get the player at a specific square or to verify if it is occupied the square.
 */
sealed interface BoardDraw : Board