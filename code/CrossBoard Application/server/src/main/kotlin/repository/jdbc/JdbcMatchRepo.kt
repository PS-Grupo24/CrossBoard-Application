package repository.jdbc

import repository.interfaces.MatchRepository
import javax.sql.DataSource
import com.google.gson.Gson
import domain.*
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class JdbcMatchRepo(private val jdbc: DataSource): MatchRepository {

    override fun addMatch(match: MultiPlayerMatch): Int = transaction(jdbc) { connection ->
        val serializedBoard = Gson().toJson(match.board)
        val prepared = connection.prepareStatement("INSERT INTO match (board, player1, player2, gametype, version) VALUES (CAST(? AS jsonb), ?, null, ?, ?)", Statement.RETURN_GENERATED_KEYS).apply {
            setString(1, serializedBoard)
            setInt(2, match.player1)
            setString(3, match.gameType.name)
            setInt(4, match.version)
            executeUpdate()
        }
        getIdStatement(prepared).toInt()
    }

    override fun getRunningMatchByUser(userId:Int): MultiPlayerMatch? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT * FROM match WHERE player1 = ? OR player2 = ?").apply {
            setInt(1, userId)
            setInt(2, userId)
        }

        prepared.executeQuery().use { rs ->
            while(rs.next()) {
                val board = Gson().fromJson(rs.getString("board"), Board::class.java)
                if(board is BoardRun) {
                     return@use MultiPlayerMatch(
                        board,
                        rs.getInt("id"),
                        rs.getInt("player1"),
                        rs.getInt("player2"),
                        rs.getString("gametype").toGameType() ?: throw SQLException("Invalid game type"),
                        rs.getInt("version")
                    )
                }
            }
            return@use null
        }
    }

    override fun getMatchById(matchId:Int): MultiPlayerMatch? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT * FROM match WHERE id = ?").apply {
            setInt(1, matchId)
        }

        prepared.executeQuery().use { rs ->
            if(rs.next()) multiplayerMatchResult(rs) else null
        }
    }

    override fun getWaitingMatch(gameType: GameType): MultiPlayerMatch? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT * FROM match WHERE player2 IS NULL AND gametype = ?").apply {
            setString(1, gameType.name)
        }

        prepared.executeQuery().use { rs ->
            if(rs.next()) multiplayerMatchResult(rs) else null
        }
    }

    override fun updateMatch(matchId: Int, board: Board, player1: Int, player2: Int?, gameType: GameType, version: Int): MultiPlayerMatch = transaction(jdbc) { connection ->
        val serializedBoard = Gson().toJson(board)
        val prepared = connection.prepareStatement("UPDATE match SET board = CAST(? AS jsonb), player1 = ?, player2 = ?, gametype = ?, version = ? WHERE id = ?").apply {
            setString(1, serializedBoard)
            setInt(2, player1)
            setInt(3, player2 ?: 0)
            setString(4, gameType.name)
            setInt(5, version)
            setInt(6, matchId)
            executeUpdate()
        }

        MultiPlayerMatch(
            board,
            matchId,
            player1,
            player2,
            gameType,
            version
        )
    }
}

private fun multiplayerMatchResult(rs: ResultSet) = MultiPlayerMatch(
    Gson().fromJson(rs.getString("board"), Board::class.java),
    rs.getInt("id"),
    rs.getInt("player1"),
    rs.getInt("player2"),
    rs.getString("gametype").toGameType() ?: throw SQLException("Invalid game type"),
    rs.getInt("version")
)