package service

import domain.Move
import model.GameType
import model.MultiPlayerMatch
import repository.interfaces.MatchRepository

class MatchService(private val matchRepository: MatchRepository) {
    suspend fun enterMatch(userId: UInt, gameType: GameType): MultiPlayerMatch{
        if (matchRepository.getRunningMatchByUser(userId) != null)
            throw IllegalStateException("User is currently in a running match or Waiting")
        val waitingMatch = matchRepository.getWaitingMatch(gameType)
        if (waitingMatch != null){
            val updatedMatch = MultiPlayerMatch(waitingMatch.board, waitingMatch.id, waitingMatch.player1, userId, waitingMatch.gameType)
            matchRepository.updateMatch(updatedMatch)
            return updatedMatch
        }
        val m = MultiPlayerMatch.startGame(userId, gameType)
        matchRepository.addMatch(m)
        return m
    }

    suspend fun getMatchById(matchId: UInt): MultiPlayerMatch {
        return matchRepository.getMatchById(matchId) ?: throw IllegalArgumentException("Match not found")
    }

    suspend fun getMatchByUser(userId: UInt): MultiPlayerMatch {
        return matchRepository.getRunningMatchByUser(userId) ?: throw IllegalArgumentException("Match not found")
    }

    suspend fun getWaitingMatch(gameType: GameType): MultiPlayerMatch {
        return matchRepository.getWaitingMatch(gameType) ?: throw IllegalArgumentException("Match not found")
    }

    suspend fun playMatch(matchId: UInt, userId: UInt, move: Move): MultiPlayerMatch {
        val match = getMatchById(matchId)
        if (match.player1 != userId && match.player2 != userId)
            throw IllegalArgumentException("Player is not in the match")
        val updatedMatch = match.play(move)
        matchRepository.updateMatch(updatedMatch)
        return updatedMatch
    }

}