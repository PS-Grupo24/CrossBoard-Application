package com.crossBoard.repository.memoryRepositories

import com.crossBoard.domain.board.Board
import com.crossBoard.domain.board.BoardRun
import com.crossBoard.domain.board.BoardWin
import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.MultiPlayerMatch
import com.crossBoard.httpModel.MatchCancel
import com.crossBoard.httpModel.MatchStats
import com.crossBoard.repository.interfaces.MatchRepository

/**
 * Class "MemoryMatchRep" represents the memory repository of the match.
 * @implements MatchRepository the match repository.
 */
class MemoryMatchRep: MatchRepository {

    /**
     * List of matches stored in memory.
     */
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
        matches.find { (it.user1 == userId || it.user2 == userId) && it.board is BoardRun }

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
        return matches.find { match -> match.user2 == null && match.matchType == matchType }
    }

    /**
     * Function "updateMatch" responsible to update the match information.
     * @param matchId the id of the match.
     * @param board the board of the match.
     * @param player1 the first player.
     * @param player2 the second player.
     * @param matchType the type of the game.
     */
    override fun updateMatch(matchId: Int, board: Board, player1: Int, player2: Int?, matchType: MatchType, version: Int, state: MatchState, winner: Int?): MultiPlayerMatch {
        val m = getMatchById(matchId)
        matches.remove(m)
        val match = MultiPlayerMatch(board, matchId, state, player1, player2, matchType, version, winner)
        matches.add(match)
        return match
    }

    /**
     * Function "cancelSearch" responsible for canceling a match.
     * @param userId The id of the user canceling the search.
     * @param matchId The id of the match to be canceled.
     */
    override fun cancelSearch(userId: Int, matchId: Int): MatchCancel {
        matches.removeIf { it.id == matchId }
        return MatchCancel(
            userId,
            matchId
        )
    }

    /**
     * Function "getStatistics" responsible for getting match statistics of a user.
     * @param userId The id of the user to get the statistics of.
     */
    override fun getStatistics(userId: Int): List<MatchStats> {
        val statsList = mutableListOf<MatchStats>()
        for (matchType in MatchType.entries) {
            val matchList = matches.filter {
                it.matchType == matchType &&
                        (it.user1 == userId || it.user2 == userId)
            }
            val matchCount = matchList.size
            val drawCount = matchList.count { it.state == MatchState.DRAW }
            val winCount = matchList.count { it.state == MatchState.WIN && (it.board as BoardWin).winner == it.getPlayerType(userId) }
            val lossCount = matchCount - drawCount - winCount
            val stats = MatchStats(
                matchType.name,
                matchCount,
                winCount,
                drawCount,
                lossCount,
                if (matchCount == 0) 0.0 else winCount.toDouble()/matchCount.toDouble()
            )
            statsList.add(stats)
        }
        return statsList.toList()
    }


}