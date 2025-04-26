package crossBoard.repository.interfaces

import crossBoard.domain.Board
import crossBoard.domain.MatchState
import crossBoard.domain.MatchType
import crossBoard.domain.MultiPlayerMatch

/**
 * Interface "MatchRepository" represents the repository of the match.
 */
interface MatchRepository {
    //Function responsible to add the match to the list of matches.
    fun addMatch(match: MultiPlayerMatch): Int
    //Function responsible to get the running match by the user.
    fun getRunningMatchByUser(userId:Int): MultiPlayerMatch?
    //Function responsible to get the match by its id.
    fun getMatchById(matchId:Int): MultiPlayerMatch?
    //Function responsible to get the waiting match.
    fun getWaitingMatch(matchType: MatchType): MultiPlayerMatch?
    //Function responsible to update the match information.
    fun updateMatch(matchId: Int, board: Board, player1: Int, player2: Int?, matchType: MatchType, version: Int, state: MatchState): MultiPlayerMatch
}