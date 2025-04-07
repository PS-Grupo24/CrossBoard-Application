package repository.interfaces

import domain.Board
import domain.GameType
import domain.MultiPlayerMatch

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
    fun getWaitingMatch(gameType: GameType): MultiPlayerMatch?
    //Function responsible to update the match information.
    fun updateMatch(matchId: Int, board: Board, player1: Int, player2: Int?, gameType: GameType, version: Int): MultiPlayerMatch
}