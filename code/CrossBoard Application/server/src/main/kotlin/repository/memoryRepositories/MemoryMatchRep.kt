package repository.memoryRepositories

import domain.BoardRun
import model.GameType
import model.MultiPlayerMatch
import repository.interfaces.MatchRepository
import domain.Board

/**
 * Class "MemoryMatchRep" represents the memory repository of the match.
 * @implements MatchRepository the match repository.
 */
class MemoryMatchRep: MatchRepository {
    //Value storing the list of matches of the application.
    private val matches = mutableListOf<MultiPlayerMatch>()

    /**
     * Function "addMatch" responsible to add the match to the list of matches.
     * @param match represents the match information to be added.
     * @return UInt the match id that was added.
     */
    override fun addMatch(match: MultiPlayerMatch): UInt {
        matches.add(match)
        return match.id
    }

    /**
     * Function "getRunningMatchByUser" responsible to get the running match by the user.
     * @param userId the id of the user.
     * @return MultiPlayerMatch? the running match if found, null otherwise.
     */
    override fun getRunningMatchByUser(userId: UInt): MultiPlayerMatch? =
        matches.find { (it.player1 == userId || it.player2 == userId) && it.board is BoardRun}

    /**
     * Function "getMatchById" responsible to get the match by its id.
     * @param matchId the id of the match.
     * @return MultiPlayerMatch? the match if found, null otherwise.
     */
    override fun getMatchById(matchId: UInt): MultiPlayerMatch? = matches.find { it.id == matchId }

    /**
     * Function "getWaitingMatch" responsible to get the waiting match.
     * @param gameType the type of the game.
     * @return MultiPlayerMatch? the waiting match if found, null otherwise.
     */
    override fun getWaitingMatch(gameType: GameType): MultiPlayerMatch? {
        return matches.find { match -> match.player2 == null && match.gameType == gameType }
    }

    /**
     * Function "updateMatch" responsible to update the match information.
     * @param matchId the id of the match.
     * @param board the board of the match.
     * @param player1 the first player.
     * @param player2 the second player.
     * @param gametype the type of the game.
     */
    override fun updateMatch(matchId: UInt, board: Board, player1: UInt, player2: UInt?, gametype: GameType): MultiPlayerMatch {
        val m = getMatchById(matchId)
        matches.remove(m)
        val match = MultiPlayerMatch(board, matchId, player1, player2, gametype)
        matches.add(match)
        return match
    }
}