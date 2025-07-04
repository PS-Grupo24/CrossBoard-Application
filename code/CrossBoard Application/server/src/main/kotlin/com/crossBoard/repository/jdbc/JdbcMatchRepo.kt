package com.crossBoard.repository.jdbc

import com.crossBoard.domain.MatchState
import com.crossBoard.domain.MatchType
import com.crossBoard.domain.MultiPlayerMatch
import com.crossBoard.domain.board.*
import com.crossBoard.domain.toMatchState
import com.crossBoard.domain.toMatchType
import com.google.gson.Gson
import com.crossBoard.httpModel.MatchCancel
import com.crossBoard.httpModel.MatchStats
import com.crossBoard.repository.interfaces.MatchRepository
import javax.sql.DataSource
import java.sql.ResultSet
import java.sql.Statement

/**
 * Manages the transactions with the database for the Match entity.
 * @param jdbc The datasource that provides the connection with the database.
 */
class JdbcMatchRepo(private val jdbc: DataSource): MatchRepository {

    /**
     * Function "addMatch" responsible to add the match to the list of matches.
     * @param match represents the match information to be added.
     * @return Int the match id that was added.
     */
    override fun addMatch(match: MultiPlayerMatch): Int = transaction(jdbc) { connection ->
        val serializedBoard = Gson().toJson(match.board)
        val prepared = connection.prepareStatement("INSERT INTO match (id ,board, player1, player2, match_type, version, state, winner) VALUES (?,CAST(? AS jsonb), ?, null, ?, ?, ?, null)", Statement.RETURN_GENERATED_KEYS).apply {
            setInt(1, match.id)
            setString(2, serializedBoard)
            setInt(3, match.user1)
            setString(4, match.matchType.toString())
            setInt(5, match.version)
            setString(6, match.state.toString())
            executeUpdate()
        }
        return@transaction getIdStatement(prepared).toInt()
    }

    /**
     * Function "getRunningMatchByUser" responsible to get the running match by the user.
     * @param userId the id of the user.
     * @return MultiPlayerMatch? the running match if found, null otherwise.
     */
    override fun getRunningMatchByUser(userId:Int): MultiPlayerMatch? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT * FROM match WHERE (player1 = ? OR player2 = ?) AND (state = ? OR state = ?)").apply {
            setInt(1, userId)
            setInt(2, userId)
            setString(3, MatchState.RUNNING.toString())
            setString(4, MatchState.WAITING.toString())
        }

        prepared.executeQuery().use { rs ->
            if(rs.next()) {
                val gameType = rs.getString("match_type").toMatchType()
                val state = rs.getString("state").toMatchState()
                val board = getBoard(gameType, state, rs.getString("board"))
                val plyr2Value = rs.getInt("player2")
                val player2 = if (plyr2Value == 0) null else plyr2Value
                val winnerValue = rs.getInt("winner")
                val winner = if (winnerValue == 0) null else winnerValue
                     return@transaction MultiPlayerMatch(
                        board,
                        rs.getInt("id"),
                         state,
                        rs.getInt("player1"),
                        player2,
                        gameType,
                        rs.getInt("version"),
                         winner
                    )
            }
            return@transaction null
        }
    }

    /**
     * Function "getMatchById" responsible to get the match by its id.
     * @param matchId the id of the match.
     * @return MultiPlayerMatch? the match if found, null otherwise.
     */
    override fun getMatchById(matchId:Int): MultiPlayerMatch? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT * FROM match WHERE id = ?").apply {
            setInt(1, matchId)
        }

        prepared.executeQuery().use { rs ->
            return@transaction if(rs.next()) multiplayerMatchResult(rs) else null
        }
    }

    /**
     * Function "getWaitingMatch" responsible to get the waiting match.
     * @param matchType the type of the game.
     * @return MultiPlayerMatch? the waiting match if found, null otherwise.
     */
    override fun getWaitingMatch(matchType: MatchType): MultiPlayerMatch? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT * FROM match WHERE player2 IS NULL AND match_type = ? LIMIT 1").apply {
            setString(1, matchType.toString())
        }

        prepared.executeQuery().use { rs ->
            if(rs.next()) multiplayerMatchResult(rs) else null
        }
    }

    /**
     * Function "updateMatch" responsible to update the match information.
     * @param matchId the id of the match.
     * @param board the board of the match.
     * @param player1 the first player.
     * @param player2 the second player.
     * @param matchType the type of the game.
     */
    override fun updateMatch(matchId: Int, board: Board, player1: Int, player2: Int?, matchType: MatchType, version: Int, state: MatchState, winner: Int?): MultiPlayerMatch = transaction(jdbc) { connection ->
        val serializedBoard = Gson().toJson(board)
        connection.prepareStatement("UPDATE match SET board = CAST(? AS jsonb), state = ? ,player1 = ?, player2 = ?, match_type = ?, version = ?, winner = ? WHERE id = ?").apply {
            setString(1, serializedBoard)
            setString(2, state.toString())
            setInt(3, player1)
            setInt(4, player2 ?: 0)
            setString(5, matchType.toString())
            setInt(6, version)
            setInt(7, winner?:0)
            setInt(8, matchId)
            executeUpdate()
        }

        return@transaction MultiPlayerMatch(
            board,
            matchId,
            state,
            player1,
            player2,
            matchType,
            version,
            winner
        )
    }

    /**
     * Function "cancelSearch" responsible for canceling a match.
     * @param userId The id of the user canceling the search.
     * @param matchId The id of the match to be canceled.
     */
    override fun cancelSearch(userId: Int, matchId:Int): MatchCancel = transaction(jdbc){ connection ->
        connection.prepareStatement("DELETE FROM match WHERE id = ?").apply {
            setInt(1, matchId)
            executeUpdate()
        }

        return@transaction MatchCancel(
            userId, matchId
        )
    }

    /**
     * Function "getStatistics" responsible for getting match statistics of a user.
     * @param userId The id of the user to get the statistics of.
     */
    override fun getStatistics(userId: Int): List<MatchStats> = transaction(jdbc){ connection ->
        val statsList = mutableListOf<MatchStats>()
        for (matchType in MatchType.entries) {
            val matches = mutableListOf<MultiPlayerMatch>()
            val prepared = connection.prepareStatement("SELECT * FROM match WHERE match_type = ? AND (player1 = ? OR player2 = ?)").apply {
                setString(1, matchType.value)
                setInt(2, userId)
                setInt(3, userId)
            }

            prepared.executeQuery().use{ rs ->
                while(rs.next()){
                    val match = multiplayerMatchResult(rs)
                    matches.add(match)
                }
            }
            val totalMatches = matches.size
            val totalWins = matches.count{it.winner == userId}
            val totalDraws = matches.count{it.state == MatchState.DRAW}
            val stat = MatchStats(
                matchType.name,
                totalMatches,
                totalWins,
                totalDraws,
                totalMatches - totalWins - totalDraws,
                if (totalMatches == 0) 0.0 else totalWins.toDouble()/totalMatches.toDouble()
            )
            statsList.add(stat)
        }
        return@transaction statsList.toList()
    }
}

/**
 * Private auxiliary function responsible for converting a ResultSet from match table into MultiPlayerMatch type.
 * @param rs The match result set.
 */
private fun multiplayerMatchResult(rs: ResultSet): MultiPlayerMatch {
    val matchType = rs.getString("match_type").toMatchType()
    val state = rs.getString("state").toMatchState()
    val board = getBoard(matchType, state, rs.getString("board"))
    val p2Value = rs.getInt("player2")
    val player2 = if (p2Value == 0) null else p2Value
    val winnerValue = rs.getInt("winner")
    val winner = if (winnerValue == 0) null else winnerValue
    return MultiPlayerMatch(
        board,
        rs.getInt("id"),
        state,
        rs.getInt("player1"),
        player2,
        matchType,
        rs.getInt("version"),
        winner
    )
}

/**
 * Responsible for deserializing the board into the correct type.
 * @param matchType The type of the match to deserialize into.
 * @param state The state of the match to deserialize into.
 * @param serializedBoard The serialized board.
 */
private fun getBoard(matchType: MatchType, state: MatchState, serializedBoard: String): Board {
    when(matchType) {
        MatchType.TicTacToe -> {
            return when(state){
                MatchState.WAITING -> Gson().fromJson(serializedBoard, TicTacToeBoardRun::class.java)
                MatchState.RUNNING -> Gson().fromJson(serializedBoard, TicTacToeBoardRun::class.java)
                MatchState.DRAW -> Gson().fromJson(serializedBoard, TicTacToeBoardDraw::class.java)
                MatchState.WIN -> Gson().fromJson(serializedBoard, TicTacToeBoardWin::class.java)
            }
        }
        MatchType.Reversi -> {
            return when(state){
                MatchState.WAITING -> Gson().fromJson(serializedBoard, ReversiBoardRun::class.java)
                MatchState.RUNNING -> Gson().fromJson(serializedBoard, ReversiBoardRun::class.java)
                MatchState.DRAW -> Gson().fromJson(serializedBoard, ReversiBoardDraw::class.java)
                MatchState.WIN -> Gson().fromJson(serializedBoard, ReversiBoardWin::class.java)
            }
        }
    }
}