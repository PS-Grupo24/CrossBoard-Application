package service

import domain.Move
import model.GameType
import model.MultiPlayerMatch
import repository.interfaces.MatchRepository
import util.ApiError
import util.Either

class MatchService(private val matchRepository: MatchRepository) {
    suspend fun enterMatch(userId: UInt, gameType: GameType): Either<ApiError, MultiPlayerMatch>{
        if (matchRepository.getRunningMatchByUser(userId) != null) return Either.Left(ApiError.USER_ALREADY_IN_MATCH)
        val waitingMatch = matchRepository.getWaitingMatch(gameType)
        if (waitingMatch != null){
            val updatedMatch = MultiPlayerMatch(waitingMatch.board, waitingMatch.id, waitingMatch.player1, userId, waitingMatch.gameType)
            matchRepository.updateMatch(updatedMatch.id, updatedMatch.board, updatedMatch.player1, updatedMatch.player2, updatedMatch.gameType)
            return Either.Right(updatedMatch)
        }
        val m = MultiPlayerMatch.startGame(userId, gameType)
        matchRepository.addMatch(m.board, m.player1, m.player2, m.gameType)
        return Either.Right(m)
    }

    suspend fun getMatchById(matchId: UInt): Either<ApiError, MultiPlayerMatch> =
        when(val m = matchRepository.getMatchById(matchId)){
            null -> Either.Left(ApiError.MATCH_NOT_FOUND)
            else -> Either.Right(m)
        }


    suspend fun getMatchByUser(userId: UInt): Either<ApiError, MultiPlayerMatch> =
        when(val match = matchRepository.getRunningMatchByUser(userId)){
            null -> Either.Left(ApiError.MATCH_NOT_FOUND)
            else -> Either.Right(match)
        }

    suspend fun getWaitingMatch(gameType: GameType): Either<ApiError, MultiPlayerMatch> =
        when(val match = matchRepository.getWaitingMatch(gameType)){
            null -> Either.Left(ApiError.MATCH_NOT_FOUND)
            else -> Either.Right(match)
        }

    suspend fun playMatch(matchId: UInt, userId: UInt, move: Move): Either<ApiError, MultiPlayerMatch> {
        val match = matchRepository.getMatchById(matchId) ?: return Either.Left(ApiError.MATCH_NOT_FOUND)
        if (match.player1 != userId && match.player2 != userId)
            return Either.Left(ApiError.USER_NOT_IN_THIS_MATCH)
        val updatedMatch = match.play(move)
        matchRepository.updateMatch(updatedMatch.id, updatedMatch.board, updatedMatch.player1, updatedMatch.player2, updatedMatch.gameType)
        return Either.Right(updatedMatch)
    }

    suspend fun forfeit(matchId: UInt, userId: UInt): Either<ApiError, MultiPlayerMatch> {
        val match = matchRepository.getMatchById(matchId) ?: return Either.Left(ApiError.MATCH_NOT_FOUND)
        if (match.player1 != userId && match.player2 != userId)
            return Either.Left(ApiError.USER_NOT_IN_THIS_MATCH)
        val forfeitedMatch = match.forfeit(userId)
        matchRepository.updateMatch(forfeitedMatch.id, forfeitedMatch.board, forfeitedMatch.player1, forfeitedMatch.player2, forfeitedMatch.gameType)
        return Either.Right(forfeitedMatch)
    }

}