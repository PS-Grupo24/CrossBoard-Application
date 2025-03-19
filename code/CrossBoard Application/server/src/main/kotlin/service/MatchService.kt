package service

import model.GameType
import model.MultiPlayerMatch
import repository.interfaces.MatchRepository

class MatchService(private val matchRepository: MatchRepository) {
    suspend fun enterMatch(userId: UInt, gameType: GameType): MultiPlayerMatch{
        if (matchRepository.getRunningMatchByUser(userId) != null)
            throw IllegalStateException("User is currently in a running match or Waiting")
        val m = MultiPlayerMatch.startGame(userId, gameType)
        matchRepository.addMatch(m)
        return m
    }
}