package repository.jdbc

import com.google.gson.Gson
import repository.interfaces.MatchRepository
import javax.sql.DataSource
import domain.*
import java.sql.ResultSet
import java.sql.Statement

class JdbcMatchRepo(private val jdbc: DataSource): MatchRepository {

    override fun addMatch(match: MultiPlayerMatch): Int = transaction(jdbc) { connection ->
        val serializedBoard = Gson().toJson(match.board)
        val prepared = connection.prepareStatement("INSERT INTO match (id ,board, player1, player2, match_type, version, state) VALUES (?,CAST(? AS jsonb), ?, null, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS).apply {
            setInt(1, match.id)
            setString(2, serializedBoard)
            setInt(3, match.player1)
            setString(4, match.matchType.toString())
            setInt(5, match.version)
            setString(6, match.state.toString())
            executeUpdate()
        }
        return@transaction getIdStatement(prepared).toInt()
    }

    override fun getRunningMatchByUser(userId:Int): MultiPlayerMatch? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT * FROM match WHERE (player1 = ? OR player2 = ?) AND state = ?").apply {
            setInt(1, userId)
            setInt(2, userId)
            setString(3, MatchState.RUNNING.toString())
        }

        prepared.executeQuery().use { rs ->
            if(rs.next()) {
                val gameType = rs.getString("match_type").toMatchType()
                val state = rs.getString("state").toMatchState()
                val board = getBoard(gameType, state, rs.getString("board"))
                     return@use MultiPlayerMatch(
                        board,
                        rs.getInt("id"),
                         state,
                        rs.getInt("player1"),
                        rs.getInt("player2"),
                        gameType,
                        rs.getInt("version")
                    )
            }
            return@use null
        }
    }

    override fun getMatchById(matchId:Int): MultiPlayerMatch? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT * FROM match WHERE id = ?").apply {
            setInt(1, matchId)
        }

        prepared.executeQuery().use { rs ->
            return@transaction if(rs.next()) multiplayerMatchResult(rs) else null
        }
    }

    override fun getWaitingMatch(matchType: MatchType): MultiPlayerMatch? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT * FROM match WHERE player2 IS NULL AND match_type = ? LIMIT 1").apply {
            setString(1, matchType.toString())
        }

        prepared.executeQuery().use { rs ->
            if(rs.next()) multiplayerMatchResult(rs) else null
        }
    }

    override fun updateMatch(matchId: Int, board: Board, player1: Int, player2: Int?, matchType: MatchType, version: Int, state: MatchState): MultiPlayerMatch = transaction(jdbc) { connection ->
        val serializedBoard = Gson().toJson(board)
        connection.prepareStatement("UPDATE match SET board = CAST(? AS jsonb), state = ? ,player1 = ?, player2 = ?, match_type = ?, version = ? WHERE id = ?").apply {
            setString(1, serializedBoard)
            setString(2, state.toString())
            setInt(3, player1)
            setInt(4, player2 ?: 0)
            //if (player2 == null) setNull(4) else setInt(4, player2)
            setString(5, matchType.toString())
            setInt(6, version)
            setInt(7, matchId)
            executeUpdate()
        }

        return@transaction MultiPlayerMatch(
            board,
            matchId,
            state,
            player1,
            player2,
            matchType,
            version
        )
    }
}

private fun multiplayerMatchResult(rs: ResultSet): MultiPlayerMatch {
    val matchType = rs.getString("match_type").toMatchType()
    val state = rs.getString("state").toMatchState()
    val board = getBoard(matchType, state, rs.getString("board"))
    val p2Value = rs.getInt("player2")
    val player2 = if (p2Value == 0) null else p2Value
    return MultiPlayerMatch(
        board,
        rs.getInt("id"),
        state,
        rs.getInt("player1"),
        player2,
        matchType,
        rs.getInt("version")
    )
}


private fun getBoard(matchType: MatchType, state: MatchState, serializedBoard: String): Board {
    when(matchType) {
        MatchType.TicTacToe -> {
            return when(state){
                MatchState.RUNNING -> Gson().fromJson(serializedBoard, TicTacToeBoardRun::class.java)
                MatchState.DRAW -> Gson().fromJson(serializedBoard, TicTacToeBoardDraw::class.java)
                MatchState.WIN -> Gson().fromJson(serializedBoard, TicTacToeBoardWin::class.java)
            }
        }
    }
}