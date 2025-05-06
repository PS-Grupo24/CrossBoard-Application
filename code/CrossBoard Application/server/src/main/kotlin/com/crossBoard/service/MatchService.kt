package com.crossBoard.service

import com.crossBoard.domain.MatchState
import com.crossBoard.domain.Move
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.MultiPlayerMatch
import com.crossBoard.httpModel.MatchCancelOutput
import com.crossBoard.repository.interfaces.MatchRepository
import com.crossBoard.triggerAutoForfeit
import com.crossBoard.util.ApiError
import com.crossBoard.util.Either
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

class MatchService(private val matchRepository: MatchRepository) {

    private val turnTimers = ConcurrentHashMap<Int, Job>()

    fun enterMatch(userId: Int, matchType: MatchType): Either<ApiError, MultiPlayerMatch> {
        if (matchRepository.getRunningMatchByUser(userId) != null) return Either.Left(ApiError.USER_ALREADY_IN_MATCH)
        val waitingMatch = matchRepository.getWaitingMatch(matchType)
        println(waitingMatch)
        if (waitingMatch != null){
            val updatedMatch = waitingMatch.join(userId)
            matchRepository.updateMatch(
                updatedMatch.id,
                updatedMatch.board,
                updatedMatch.player1,
                updatedMatch.player2,
                updatedMatch.matchType,
                updatedMatch.version,
                updatedMatch.state
            )
            if (updatedMatch.isMyTurn(userId)) startTurnTimer(updatedMatch.id, userId)
            else startTurnTimer(
                updatedMatch.id,
                updatedMatch.otherPlayer(userId)
            )
            return Either.Right(updatedMatch)
        }
        val m = MultiPlayerMatch.startGame(userId, matchType)
        matchRepository.addMatch(m)
        return Either.Right(m)
    }

    fun getMatchById(matchId: Int): Either<ApiError, MultiPlayerMatch> =
        when(val m = matchRepository.getMatchById(matchId)){
            null -> Either.Left(ApiError.MATCH_NOT_FOUND)
            else -> Either.Right(m)
        }

    fun getMatchByVersion(matchId: Int, version: Int): Either<ApiError, MultiPlayerMatch> {
        val m = matchRepository.getMatchById(matchId) ?: return Either.Left(ApiError.MATCH_NOT_FOUND)
        if (m.version < version) return Either.Left(ApiError.VERSION_MISMATCH)
        return Either.Right(m)
    }

    fun getMatchByUser(userId: Int): Either<ApiError, MultiPlayerMatch> =
        when(val match = matchRepository.getRunningMatchByUser(userId)){
            null -> Either.Left(ApiError.MATCH_NOT_FOUND)
            else -> Either.Right(match)
        }

    fun getWaitingMatch(matchType: MatchType): Either<ApiError, MultiPlayerMatch> =
        when(val match = matchRepository.getWaitingMatch(matchType)){
            null -> Either.Left(ApiError.MATCH_NOT_FOUND)
            else -> Either.Right(match)
        }

    fun playMatch(matchId: Int, userId: Int, move: Move, version: Int): Either<ApiError, MultiPlayerMatch> {
        val match = matchRepository.getMatchById(matchId) ?: return Either.Left(ApiError.MATCH_NOT_FOUND)
        if (match.player1 != userId && match.player2 != userId)
            return Either.Left(ApiError.USER_NOT_IN_THIS_MATCH)
        if(match.version != version)
            return Either.Left(ApiError.VERSION_MISMATCH)

        cancelTurnTimer(matchId)

        val p = if (match.player1 == userId) match.board.player1 else match.board.player2
        if (p != move.player) return Either.Left(ApiError.INCORRECT_PLAYER_TYPE_FOR_THIS_USER)
        val updatedMatch = match.play(move)
        matchRepository.updateMatch(
            updatedMatch.id,
            updatedMatch.board,
            updatedMatch.player1,
            updatedMatch.player2,
            updatedMatch.matchType,
            updatedMatch.version,
            updatedMatch.state
        )
        if (updatedMatch.state == MatchState.RUNNING) startTurnTimer(updatedMatch.id, updatedMatch.otherPlayer(userId))
        else cancelTurnTimer(updatedMatch.id)
        return Either.Right(updatedMatch)
    }

    fun forfeit(matchId: Int, userId: Int): Either<ApiError, MultiPlayerMatch> {
        val match = matchRepository.getMatchById(matchId) ?: return Either.Left(ApiError.MATCH_NOT_FOUND)
        if (match.player1 != userId && match.player2 != userId)
            return Either.Left(ApiError.USER_NOT_IN_THIS_MATCH)

        cancelTurnTimer(matchId)

        val forfeitedMatch = match.forfeit(userId)
        matchRepository.updateMatch(
            forfeitedMatch.id,
            forfeitedMatch.board,
            forfeitedMatch.player1,
            forfeitedMatch.player2,
            forfeitedMatch.matchType,
            forfeitedMatch.version,
            forfeitedMatch.state
        )
        return Either.Right(forfeitedMatch)
    }

    fun cancelSearch(userId:Int, matchId: Int): Either<ApiError, MatchCancelOutput> {
        val match = matchRepository.getMatchById(matchId) ?: return Either.Left(ApiError.MATCH_NOT_FOUND)
        if (match.player1 != userId && match.player2 != userId) return Either.Left(ApiError.USER_NOT_IN_THIS_MATCH)
        if (match.state != MatchState.WAITING) return Either.Left(ApiError.MATCH_NOT_IN_WAITING_STATE)


        return Either.Right(matchRepository.cancelSearch(userId, matchId))
    }

    fun getRunningMatch(userId: Int): Either<ApiError, MultiPlayerMatch> {
        val match = matchRepository.getRunningMatchByUser(userId)
            ?: return Either.Left(ApiError.MATCH_NOT_FOUND)
        return Either.Right(match)
    }

    private fun startTurnTimer(matchId: Int, userId:Int) {
        val turnTimeOut = 30_000L

        turnTimers[matchId]?.cancel()

        val timerJob = CoroutineScope(Dispatchers.Default).launch {
            delay(turnTimeOut)

            val match = matchRepository.getMatchById(matchId)
            if (match != null && match.state == MatchState.RUNNING && match.isMyTurn(userId)) {
                triggerAutoForfeit(matchId, userId, this@MatchService)
            }
            else println("Turn timer expired but match $matchId is no longer running or it's not user $userId's turn.")

            turnTimers.remove(matchId)
        }
        turnTimers[matchId] = timerJob
    }

    private fun cancelTurnTimer(matchId: Int) {
        println("Cancelling turn timer for match $matchId")
        turnTimers[matchId]?.cancel()
        turnTimers.remove(matchId)
    }
}