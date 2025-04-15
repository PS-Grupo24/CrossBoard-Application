package repository.jdbc

import domain.*
import httpModel.UserProfileOutput
import repository.interfaces.UserRepository
import java.security.MessageDigest
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.*
import javax.sql.DataSource

class JdbcUserRepo(private val jdbc: DataSource): UserRepository {

    override fun getUserProfileById(userId:Int): UserProfileOutput? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT id, token, username, email FROM users WHERE id = ?").apply {
            setLong(1, userId.toLong())
        }

        prepared.executeQuery().use { rs ->
            if(rs.next()) userProfileOutputResult(rs) else null
        }
    }

    override fun getUserProfileByEmail(email: Email): UserProfileOutput? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT id, token, username, email FROM users WHERE email = ?").apply {
            setString(1, email.value)
        }

        prepared.executeQuery().use { rs ->
            if(rs.next()) userProfileOutputResult(rs) else null
        }
    }

    override fun getUserProfileByName(username: Username): UserProfileOutput? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT id, token, username, email FROM users WHERE username = ?").apply {
            setString(1, username.value)
        }

        prepared.executeQuery().use { rs ->
            if(rs.next()) userProfileOutputResult(rs) else null
        }
    }


    override fun deleteUser(userId: Int): Boolean = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("DELETE FROM users WHERE id = ?").apply {
            setLong(1, userId.toLong())
            executeUpdate()
        }
        true
    }

    override fun updateUser(userId: Int, username: Username?, email: Email?, password: Password?): UserProfileOutput = transaction(jdbc) { connection ->
        val selectPreparation = connection.prepareStatement("SELECT token, username, email FROM users WHERE id = ?").apply {
            setLong(1, userId.toLong())
        }

        selectPreparation.executeQuery().use { rs ->
            rs.next()

            val username = username?.value ?: rs.getString("username")
            val email = email?.value ?: rs.getString("email")
            val password = password?.value ?: rs.getString("password")

            connection.prepareStatement("UPDATE users SET username = ?, email = ?, password = ? where id = ?").apply {
                setString(1, username)
                setString(2, email)
                setString(3, password)
                setLong(4, userId.toLong())
                executeUpdate()
            }

            UserProfileOutput(
                userId,
                rs.getString("token"),
                username,
                email
            )
        }
    }

    override fun getUserFullDetails(userId: Int): User? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT * FROM users WHERE id = ?").apply {
            setLong(1, userId.toLong())
        }

        prepared.executeQuery().use { rs ->
            if(rs.next()) userResult(rs) else null
        }
    }

    override fun addUser(username: Username, email: Email, password: Password): UserProfileOutput = transaction(jdbc) { connection ->
        val token = generateTokenValue()
        val hashPassword = hashPassword(password.value)

        val prepared = connection.prepareStatement("INSERT INTO users (token, username, email, password) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS).apply {
            setString(1, token)
            setString(2, username.value)
            setString(3, email.value)
            setString(4, hashPassword)
            executeUpdate()
        }
        UserProfileOutput(getIdStatement(prepared).toInt(), username.value, email.value, token)
    }

    override fun getUserProfileByToken(token: String): UserProfileOutput? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT id, token, username, email FROM users WHERE token = ?").apply {
            setString(1, token)
        }

        prepared.executeQuery().use { rs ->
            if(rs.next()) userProfileOutputResult(rs) else null
        }
    }
}

private fun userProfileOutputResult(rs: ResultSet): UserProfileOutput {
    return UserProfileOutput(
        rs.getInt("id"),
        rs.getString("token"),
        rs.getString("username"),
        rs.getString("email")
    )
}

private fun userResult(rs: ResultSet): User {
    return User(
        rs.getInt("id"),
        Username(rs.getString("username")),
        Email(rs.getString("email")),
        Password(rs.getString("password")),
        rs.getString("token")
    )
}

private fun hashPassword(password: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val bytes = md.digest(password.toByteArray())
    return Base64.getEncoder().encodeToString(bytes)
}

fun getIdStatement(prepared: PreparedStatement): UInt = if(prepared.generatedKeys.next())
    prepared.generatedKeys.getInt(1).toUInt()
else throw SQLException("Something went wrong obtaining the id generated from the statement.")