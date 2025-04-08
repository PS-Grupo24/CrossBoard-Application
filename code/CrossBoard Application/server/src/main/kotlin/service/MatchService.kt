package service

import domain.Move
import domain.GameType
import domain.MultiPlayerMatch
import httpModel.MatchOutput
import kotlinx.coroutines.delay
import repository.interfaces.MatchRepository
import util.ApiError
import util.Either

class MatchService(private val matchRepository: MatchRepository) {
    fun enterMatch(userId: Int, gameType: GameType): Either<ApiError, MultiPlayerMatch> {
        if (matchRepository.getRunningMatchByUser(userId) != null) return Either.Left(ApiError.USER_ALREADY_IN_MATCH)
        val waitingMatch = matchRepository.getWaitingMatch(gameType)
        if (waitingMatch != null){
            val updatedMatch = MultiPlayerMatch(
                waitingMatch.board,
                waitingMatch.id,
                waitingMatch.player1,
                userId,
                waitingMatch.gameType ,
                waitingMatch.version + 1
            )
            matchRepository.updateMatch(
                updatedMatch.id,
                updatedMatch.board,
                updatedMatch.player1,
                updatedMatch.player2,
                updatedMatch.gameType,
                updatedMatch.version
            )
            return Either.Right(updatedMatch)
        }
        val m = MultiPlayerMatch.startGame(userId, gameType)
        matchRepository.addMatch(m)
        return Either.Right(m)
    }

    fun getMatchById(matchId: Int): Either<ApiError, MultiPlayerMatch> =
        when(val m = matchRepository.getMatchById(matchId)){
            null -> Either.Left(ApiError.MATCH_NOT_FOUND)
            else -> Either.Right(m)
        }

    fun getMatchByVersion(matchId: Int, version: Int): Either<ApiError, MultiPlayerMatch>{
        TODO()
    }

    fun getMatchByUser(userId: Int): Either<ApiError, MultiPlayerMatch> =
        when(val match = matchRepository.getRunningMatchByUser(userId)){
            null -> Either.Left(ApiError.MATCH_NOT_FOUND)
            else -> Either.Right(match)
        }

    fun getWaitingMatch(gameType: GameType): Either<ApiError, MultiPlayerMatch> =
        when(val match = matchRepository.getWaitingMatch(gameType)){
            null -> Either.Left(ApiError.MATCH_NOT_FOUND)
            else -> Either.Right(match)
        }

    fun playMatch(matchId: Int, userId: Int, move: Move, version: Int): Either<ApiError, Move> {
        val match = matchRepository.getMatchById(matchId) ?: return Either.Left(ApiError.MATCH_NOT_FOUND)
        if (match.player1 != userId && match.player2 != userId)
            return Either.Left(ApiError.USER_NOT_IN_THIS_MATCH)
        if(match.version != version)
            return Either.Left(ApiError.VERSION_MISMATCH)

        val p = if (match.player1 == userId) match.board.player1 else match.board.player2
        if (p != move.player) return Either.Left(ApiError.INCORRECT_PLAYER_TYPE_FOR_THIS_USER)
        val updatedMatch = match.play(move)
        matchRepository.updateMatch(updatedMatch.id, updatedMatch.board, updatedMatch.player1, updatedMatch.player2, updatedMatch.gameType, updatedMatch.version)
        return Either.Right(move)
    }

    fun forfeit(matchId: Int, userId: Int): Either<ApiError, MultiPlayerMatch> {
        val match = matchRepository.getMatchById(matchId) ?: return Either.Left(ApiError.MATCH_NOT_FOUND)
        if (match.player1 != userId && match.player2 != userId)
            return Either.Left(ApiError.USER_NOT_IN_THIS_MATCH)
        val forfeitedMatch = match.forfeit(userId)
        matchRepository.updateMatch(forfeitedMatch.id, forfeitedMatch.board, forfeitedMatch.player1, forfeitedMatch.player2, forfeitedMatch.gameType, forfeitedMatch.version)
        return Either.Right(forfeitedMatch)
    }

}