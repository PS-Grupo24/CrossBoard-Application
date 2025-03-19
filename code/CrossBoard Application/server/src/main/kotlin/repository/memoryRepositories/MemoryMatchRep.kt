package repository.memoryRepositories

import domain.BoardRun
import model.GameType
import model.MultiPlayerMatch
import repository.interfaces.MatchRepository

object MemoryMatchRep: MatchRepository {
    private val matches = mutableListOf<MultiPlayerMatch>()

    override suspend fun addMatch(match: MultiPlayerMatch): MultiPlayerMatch {
        matches.add(match)
        return match
    }

    override suspend fun getRunningMatchByUser(userId: UInt): MultiPlayerMatch? {
        return matches.find { (it.player1 == userId || it.player2 == userId) && it.board is BoardRun}
    }

    override suspend fun getMatchById(matchId: UInt): MultiPlayerMatch? {
        return matches.find { it.id == matchId }
    }

    override suspend fun getWaitingMatch(gameType: GameType): MultiPlayerMatch? {
        return matches.find { match -> match.player2 == null && match.gameType == gameType }
    }

    override suspend fun updateMatch(match: MultiPlayerMatch): MultiPlayerMatch {
        val m = matches.find { it.id == match.id }
        matches.remove(m)
        matches.add(match)
        return match
    }
}