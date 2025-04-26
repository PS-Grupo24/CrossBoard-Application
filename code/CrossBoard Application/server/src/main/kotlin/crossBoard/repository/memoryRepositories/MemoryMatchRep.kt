package crossBoard.repository.memoryRepositories

import crossBoard.domain.*
import crossBoard.httpModel.MatchCancelOutput
import crossBoard.repository.interfaces.MatchRepository

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
     * @return Int the match id that was added.
     */
    override fun addMatch(match: MultiPlayerMatch): Int {
        matches.add(match)
        return match.id
    }

    /**
     * Function "getRunningMatchByUser" responsible to get the running match by the user.
     * @param userId the id of the user.
     * @return MultiPlayerMatch? the running match if found, null otherwise.
     */
    override fun getRunningMatchByUser(userId: Int): MultiPlayerMatch? =
        matches.find { (it.player1 == userId || it.player2 == userId) && it.board is BoardRun }

    /**
     * Function "getMatchById" responsible to get the match by its id.
     * @param matchId the id of the match.
     * @return MultiPlayerMatch? the match if found, null otherwise.
     */
    override fun getMatchById(matchId: Int): MultiPlayerMatch? = matches.find { it.id == matchId }

    /**
     * Function "getWaitingMatch" responsible to get the waiting match.
     * @param matchType the type of the game.
     * @return MultiPlayerMatch? the waiting match if found, null otherwise.
     */
    override fun getWaitingMatch(matchType: MatchType): MultiPlayerMatch? {
        return matches.find { match -> match.player2 == null && match.matchType == matchType }
    }

    /**
     * Function "updateMatch" responsible to update the match information.
     * @param matchId the id of the match.
     * @param board the board of the match.
     * @param player1 the first player.
     * @param player2 the second player.
     * @param matchType the type of the game.
     */
    override fun updateMatch(matchId: Int, board: Board, player1: Int, player2: Int?, matchType: MatchType, version: Int, state: MatchState): MultiPlayerMatch {
        val m = getMatchById(matchId)
        matches.remove(m)
        val match = MultiPlayerMatch(board, matchId, state, player1, player2, matchType, version)
        matches.add(match)
        return match
    }

    override fun cancelSearch(userId: Int, matchId: Int): MatchCancelOutput {
        matches.removeIf { it.id == matchId }
        return MatchCancelOutput(
            userId,
            matchId
        )
    }
}