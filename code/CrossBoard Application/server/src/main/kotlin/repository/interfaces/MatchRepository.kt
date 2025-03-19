package repository.interfaces

import model.GameType
import model.MultiPlayerMatch

interface MatchRepository {
    suspend fun addMatch(match: MultiPlayerMatch): MultiPlayerMatch
    suspend fun updateMatch(match: MultiPlayerMatch): MultiPlayerMatch
    suspend fun getMatchByUser(userId:UInt): MultiPlayerMatch?
    suspend fun getMatchById(matchId:UInt): MultiPlayerMatch?
    suspend fun getWaitingMatch(gameType: GameType): MultiPlayerMatch?
}