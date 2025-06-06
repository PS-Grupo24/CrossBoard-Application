package com.crossBoard.domain

import com.crossBoard.httpModel.BoardOutput
import com.crossBoard.httpModel.MatchOutput
import com.crossBoard.httpModel.MatchPlayedOutput
import com.crossBoard.httpModel.PlayerOutput
import com.crossBoard.httpModel.toMoveOutput
import kotlin.random.Random

/**
 * Interface "Match" represents a Match.
 * @property id the id of the match.
 * @property board the board of the match.
 * @property state the state of the match.
 * @property getPlayerType Function to get the player type.
 */
interface Match {
    val id: Int
    val board: Board
    val state: MatchState
    fun getPlayerType(userId: Int): Player
}

/**
 * Class "MultiPlayerMatch" represents a multiplayer match.
 * @param board the board of the match.
 * @param id the id of the match.
 * @param state the state of the match.
 * @param player1 the first player.
 * @param player2 the second player.
 * @param matchType the type of the match.
 * @param version the version of the match.
 * @param winner the winner of the match.
 * @return Match the match that was created as a multiplayer match.
 */
data class MultiPlayerMatch(
    override val board: Board,
    override val id: Int,
    override val state: MatchState,
    val player1: Int,
    val player2: Int? = null,
    val matchType: MatchType,
    val version: Int,
    val winner: Int? = null,
): Match {
    companion object{

        /**
         * Function startGame responsible to start a game.
         * @param player1 the first player.
         * @param matchType the type of the game.
         * @return MultiPlayerMatch the multiplayer match that was created.
         */
        fun startGame(player1: Int, matchType: MatchType): MultiPlayerMatch {
            require(player1 > 0) { "player1 must be greater than 0" }
            require(MatchType.entries.toTypedArray().contains(matchType)) { "matchType must be a valid match type" }
            return when(matchType) {
                MatchType.TicTacToe -> {
                    val p1 = Player.random()
                    val board = TicTacToeBoardRun(
                        initialTicTacToePositions(),
                        emptyList(),
                        Player.random(),
                        p1,
                        p1.other(),
                    )
                    MultiPlayerMatch(
                        board,
                        Random.nextInt(from = 1, Int.MAX_VALUE),
                        MatchState.WAITING,
                        player1,
                        null,
                        matchType,
                        1,
                        null
                    )
                }
            }
        }
    }

    /**
     * Function join responsible to a player to join a match.
     * @param userId2 the second player.
     * @return MultiPlayerMatch the match after the player joined.
     */
    fun join(userId2: Int): MultiPlayerMatch {
        require(userId2 > 0) { "userId2 must be greater than 0" }
        require(player2 == null) { "This game is full" }
        require(userId2 != player1) { "Player2 can't be the same as Player1" }
        return MultiPlayerMatch(
            board, id, MatchState.RUNNING, player1, userId2, matchType, version + 1, null
        )
    }

    /**
     * Function play responsible to play a move on the match.
     * @param move The move to be made.
     * @return MultiPlayerMatch the match after the move was played.
     */
    fun play(move: Move): MultiPlayerMatch {
        val newBoard = board.play(move)
        val winnerType = if (newBoard is BoardWin) newBoard.winner else null
        val player1Type = getPlayerType(player1)
        val winner = if (winnerType == player1Type) player1 else if (winnerType == player1Type.other()) player2 else null
        return MultiPlayerMatch(
            newBoard,
            id,
            getMatchStateFromBoard(player2, newBoard),
            player1,
            player2,
            matchType,
            version + 1,
            winner = winner,
        )
    }

    /**
     * Function forfeit responsible to forfeit the match.
     * @param player the player that is forfeiting.
     * @return MultiPlayerMatch the match after the forfeit.
     */
    fun forfeit(player: Int): MultiPlayerMatch {
        require(player > 0) { "player must be greater than 0" }
        val playerType = getPlayerType(player)
        val newBoard = board.forfeit(playerType)
        return MultiPlayerMatch(
            newBoard,
            id,
            getMatchStateFromBoard(player2, newBoard),
            player1,
            player2,
            matchType,
            version + 1,
            winner = if(player == player1) player2 else player1
        )
    }

    /**
     * Function getPlayerType responsible to get the player type.
     * @param userId id of the player.
     * @return Player the player type.
     */
    override fun getPlayerType(userId: Int): Player {
        require(userId == player1 || userId == player2) {"This user is not a player in this match"}
        return when(userId) {
            player1 -> board.player1
            else -> board.player2
        }
    }

    /**
     * Function equals responsible to compared MultiplayerMatch objects.
     * @param other the other object to be compared.
     * @return Boolean true if the objects are equal, false otherwise.
     */
    override fun equals(other: Any?) = other is MultiPlayerMatch && id == other.id && other.version == version

    /**
     * Function hashCode responsible to get the hash code of the object.
     * @return Int the hash code of the object.
     */
    override fun hashCode(): Int = id.hashCode() + version.hashCode()

    /**
     * Function isMyTurn responsible to check if it's the player's turn.
     * @param userId the id of the player.
     * @return Boolean true if it's the player's turn, false otherwise.
     */
    fun isMyTurn(userId: Int): Boolean {
        require(userId > 0) { "userId must be greater than 0" }
        require(userId == player1 || userId == player2) { "User is not in match" }
        val myType = if (userId == player1) board.player1 else board.player2

        return board.turn == myType
    }

    /**
     * Function otherPlayer responsible to get the other player.
     * @param userId the id of the player.
     * @return Int the id of the other player.
     */
    fun otherPlayer(userId: Int): Int {
        require(userId > 0) { "userId must be greater than 0" }
        require(userId == player1 || userId == player2) { "User is not in match" }
        require(player2 != null) { "Player2 can't be null" }

        return if (userId == player2) player1 else player2
    }
}

/**
 * Function to convert a MultiPlayerMatch to a MatchOutput.
 * @return MatchOutput the match output.
 */
fun MultiPlayerMatch.toMatchOutput(): MatchOutput {
    val winner = if (board is BoardWin) board.winner else null
    val winnerId = when(state){
        MatchState.WIN -> {
            val player1Type = getPlayerType(player1)
            if(winner == player1Type)
                player1
            else
                player2
        }
        else -> null
    }
    return MatchOutput(
        id,
        PlayerOutput(
            player1,
            getPlayerType(player1).toString()
        ),
        PlayerOutput(
            player2,
            getPlayerType(player1).other().toString()
        ),
        BoardOutput(
            winner.toString(),
            board.turn.toString(),
            board.positions.map { it.toString() },
            board.moves.map { moveToString(it) },
        ),
        matchType.toString(),
        version,
        state.toString(),
        winnerId
    )
}

/**
 * Function to convert a MultiPlayerMatch to a MatchPlayedOutput.
 * @return MatchPlayedOutput the match played output.
 */
fun MultiPlayerMatch.toPlayedMatch() = MatchPlayedOutput(this.board.moves.last().toMoveOutput(), this.version)

/*
/**
 * Class "SinglePlayerGame" represents a single player game.
 * @param board the board of the game.
 * @param user the player.
 * @param difficulty the difficulty of the game.
 * @return Game the game that was created as a single player game.
 */
class SinglePlayerMatch(
    override val board: Board,
    override val id: Int,
    override val state: MatchState,
    val user: UInt,
    difficulty: Difficulty
):
    Match {
    override fun getPlayerType(userId: Int): Player = board.player1
}*/