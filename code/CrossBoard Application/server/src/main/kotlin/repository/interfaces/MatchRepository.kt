package repository.interfaces

import domain.Board
import model.GameType
import model.MultiPlayerMatch

/**
 * Interface "MatchRepository" represents the repository of the match.
 */
interface MatchRepository {
    //Function responsible to add the match to the list of matches.
    fun addMatch(match: MultiPlayerMatch): UInt
    //Function responsible to get the running match by the user.
    fun getRunningMatchByUser(userId:UInt): MultiPlayerMatch?
    //Function responsible to get the match by its id.
    fun getMatchById(matchId:UInt): MultiPlayerMatch?
    //Function responsible to get the waiting match.
    fun getWaitingMatch(gameType: GameType): MultiPlayerMatch?
    //Function responsible to update the match information.
    fun updateMatch(matchId: UInt, board: Board, player1: UInt, player2: UInt?, gametype: GameType): MultiPlayerMatch
}