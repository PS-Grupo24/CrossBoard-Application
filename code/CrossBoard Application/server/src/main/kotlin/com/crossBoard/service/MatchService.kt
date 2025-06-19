package com.crossBoard.service

import com.crossBoard.disconnectUserEvent
import com.crossBoard.domain.MatchState
import com.crossBoard.domain.Move
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.MultiPlayerMatch
import com.crossBoard.domain.toMatchOutput
import com.crossBoard.httpModel.EventType
import com.crossBoard.httpModel.MatchCancel
import com.crossBoard.httpModel.MatchStats
import com.crossBoard.json
import com.crossBoard.repository.interfaces.MatchRepository
import com.crossBoard.sendEventToUser
import com.crossBoard.util.ApiError
import com.crossBoard.util.Either
import com.crossBoard.util.failure
import com.crossBoard.util.success
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Responsible for the match management.
 * @param matchRepository The auxiliary for communication with the database.
 */
class MatchService(private val matchRepository: MatchRepository) {
    /**
     * The play timers.
     */
    private val turnTimers = ConcurrentHashMap<Int, Job>()

    /**
     * This class scope.
     */
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * Responsible to find a match for a user.
     * @param userId The user id of the user to join a match.
     * @param matchType The type of match to find.
     * @return APIError on failure or MultiplayerMatch on success.
     */
    fun enterMatch(userId: Int, matchType: MatchType): Either<ApiError, MultiPlayerMatch> {
        if (matchRepository.getRunningMatchByUser(userId) != null) return failure(ApiError.USER_ALREADY_IN_MATCH)
        val waitingMatch = matchRepository.getWaitingMatch(matchType)
        if (waitingMatch != null){
            val updatedMatch = waitingMatch.join(userId)
            matchRepository.updateMatch(
                updatedMatch.id,
                updatedMatch.board,
                updatedMatch.player1,
                updatedMatch.player2,
                updatedMatch.matchType,
                updatedMatch.version,
                updatedMatch.state,
                updatedMatch.winner,
            )
            scope.launch {
                sendEventToUser(
                    updatedMatch.otherPlayer(userId),
                    ServerSentEvent(
                        event = EventType.MatchUpdate.name,
                        data = json.encodeToString(updatedMatch.toMatchOutput())
                    )
                )
            }
            if (updatedMatch.isMyTurn(userId)) startTurnTimer(updatedMatch.id, userId)
            else startTurnTimer(
                updatedMatch.id,
                updatedMatch.otherPlayer(userId)
            )
            return success(updatedMatch)
        }
        val m = MultiPlayerMatch.startGame(userId, matchType)
        matchRepository.addMatch(m)
        return success(m)
    }

    /**
     * Responsible for finding a match given an id.
     * @param matchId The id of the match to find.
     */
    fun getMatchById(matchId: Int): Either<ApiError, MultiPlayerMatch> =
        when(val m = matchRepository.getMatchById(matchId)){
            null -> failure(ApiError.MATCH_NOT_FOUND)
            else -> success(m)
        }

    fun getMatchByVersion(matchId: Int, version: Int): Either<ApiError, MultiPlayerMatch> {
        val m = matchRepository.getMatchById(matchId) ?: return failure(ApiError.MATCH_NOT_FOUND)
        if (m.version < version) return failure(ApiError.VERSION_MISMATCH)
        return success(m)
    }

    fun getMatchByUser(userId: Int): Either<ApiError, MultiPlayerMatch> =
        when(val match = matchRepository.getRunningMatchByUser(userId)){
            null -> failure(ApiError.MATCH_NOT_FOUND)
            else -> success(match)
        }

    fun getWaitingMatch(matchType: MatchType): Either<ApiError, MultiPlayerMatch> =
        when(val match = matchRepository.getWaitingMatch(matchType)){
            null -> failure(ApiError.MATCH_NOT_FOUND)
            else -> success(match)
        }

    fun playMatch(matchId: Int, userId: Int, move: Move, version: Int): Either<ApiError, MultiPlayerMatch> {
        val match = matchRepository.getMatchById(matchId) ?: return failure(ApiError.MATCH_NOT_FOUND)
        if (match.player1 != userId && match.player2 != userId)
            return failure(ApiError.USER_NOT_IN_THIS_MATCH)
        if(match.version != version)
            return failure(ApiError.VERSION_MISMATCH)

        cancelTurnTimer(matchId)

        val p = if (match.player1 == userId) match.board.player1 else match.board.player2
        if (p != move.player) return failure(ApiError.INCORRECT_PLAYER_TYPE_FOR_THIS_USER)
        val updatedMatch = match.play(move)
        matchRepository.updateMatch(
            updatedMatch.id,
            updatedMatch.board,
            updatedMatch.player1,
            updatedMatch.player2,
            updatedMatch.matchType,
            updatedMatch.version,
            updatedMatch.state,
            updatedMatch.winner,
        )
        scope.launch {
            sendEventToUser(
                updatedMatch.otherPlayer(userId),
                ServerSentEvent(
                    event = EventType.MatchUpdate.name,
                    data = json.encodeToString(updatedMatch.toMatchOutput())
                )
            )
            if (updatedMatch.state == MatchState.WIN || updatedMatch.state == MatchState.DRAW) {
                disconnectUserEvent(userId)
                disconnectUserEvent(updatedMatch.otherPlayer(userId))
            }
        }
        if (updatedMatch.state == MatchState.RUNNING)
            startTurnTimer(updatedMatch.id, updatedMatch.otherPlayer(userId))
        else cancelTurnTimer(updatedMatch.id)
        return success(updatedMatch)
    }

    fun forfeit(matchId: Int, userId: Int): Either<ApiError, MultiPlayerMatch> {
        val match = matchRepository.getMatchById(matchId) ?: return failure(ApiError.MATCH_NOT_FOUND)
        if (match.player1 != userId && match.player2 != userId)
            return failure(ApiError.USER_NOT_IN_THIS_MATCH)

        cancelTurnTimer(matchId)

        val forfeitedMatch = match.forfeit(userId)
        matchRepository.updateMatch(
            forfeitedMatch.id,
            forfeitedMatch.board,
            forfeitedMatch.player1,
            forfeitedMatch.player2,
            forfeitedMatch.matchType,
            forfeitedMatch.version,
            forfeitedMatch.state,
            forfeitedMatch.winner,
        )
        val opponentId = forfeitedMatch.otherPlayer(userId)
        scope.launch {
            sendEventToUser(
                userId,
                ServerSentEvent(
                    event = EventType.MatchUpdate.name,
                    data = json.encodeToString(forfeitedMatch.toMatchOutput())
                )
            )
            sendEventToUser(
                opponentId,
                ServerSentEvent(
                    event = EventType.MatchUpdate.name,
                    data = json.encodeToString(forfeitedMatch.toMatchOutput())
                )
            )
            disconnectUserEvent(userId)
            disconnectUserEvent(forfeitedMatch.otherPlayer(userId))
        }
        return success(forfeitedMatch)
    }

    fun cancelSearch(userId:Int, matchId: Int): Either<ApiError, MatchCancel> {
        val match = matchRepository.getMatchById(matchId) ?: return failure(ApiError.MATCH_NOT_FOUND)
        if (match.player1 != userId && match.player2 != userId) return failure(ApiError.USER_NOT_IN_THIS_MATCH)
        if (match.state != MatchState.WAITING) return failure(ApiError.MATCH_NOT_IN_WAITING_STATE)


        return success(matchRepository.cancelSearch(userId, matchId))
    }

    fun getRunningMatch(userId: Int): Either<ApiError, MultiPlayerMatch> {
        val match = matchRepository.getRunningMatchByUser(userId)
            ?: return failure(ApiError.MATCH_NOT_FOUND)
        return success(match)
    }

    fun getStatistics(userId: Int): List<MatchStats>{
        return matchRepository.getStatistics(userId)
    }

    private fun startTurnTimer(matchId: Int, userId:Int) {
        val turnTimeOut = 30_000L

        turnTimers[matchId]?.cancel()

        val timerJob = scope.launch {
            delay(turnTimeOut)

            val match = matchRepository.getMatchById(matchId)
            if (match != null && match.state == MatchState.RUNNING && match.isMyTurn(userId)) {
                forfeit(matchId, userId)
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