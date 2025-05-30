package com.crossBoard.repository.jdbc

import com.crossBoard.domain.Admin
import com.crossBoard.domain.Email
import com.crossBoard.domain.NormalUser
import com.crossBoard.domain.Password
import com.crossBoard.domain.Token
import com.crossBoard.domain.User
import com.crossBoard.domain.UserInfo
import com.crossBoard.domain.UserState
import com.crossBoard.domain.Username
import com.crossBoard.httpModel.UserCreationOutput
import com.crossBoard.httpModel.UserLoginOutput
import com.crossBoard.httpModel.UserProfileOutput
import com.crossBoard.repository.interfaces.UserRepository
import com.crossBoard.repository.interfaces.generateTokenValue
import com.crossBoard.repository.interfaces.hashPassword
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource

class JdbcUserRepo(private val jdbc: DataSource): UserRepository {

    override fun getUserProfileById(userId:Int): UserInfo? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT * FROM users WHERE id = ?").apply {
            setLong(1, userId.toLong())
        }

        prepared.executeQuery().use { rs ->
            if(rs.next()) userResult(rs) else null
        }
    }

    override fun getUserProfileByEmail(email: Email): UserInfo? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT * FROM users WHERE email = ?").apply {
            setString(1, email.value)
        }

        prepared.executeQuery().use { rs ->
            if(rs.next()) userResult(rs) else null
        }
    }

    override fun getUserProfileByName(username: Username): UserInfo? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT * FROM users WHERE username = ?").apply {
            setString(1, username.value)
        }

        prepared.executeQuery().use { rs ->
            if(rs.next()) userResult(rs) else null
        }
    }


    override fun deleteUser(userId: Int): Boolean = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("DELETE FROM users WHERE id = ?").apply {
            setLong(1, userId.toLong())
            executeUpdate()
        }
        true
    }

    override fun updateUser(userId: Int, username: Username?, email: Email?, password: Password?, state: UserState?): UserInfo = transaction(jdbc) { connection ->
        val selectPreparation = connection.prepareStatement("SELECT * FROM users WHERE id = ?").apply {
            setLong(1, userId.toLong())
        }

        selectPreparation.executeQuery().use { rs ->
            rs.next()

            val username = username?.value ?: rs.getString("username")
            val email = email?.value ?: rs.getString("email")
            val password = if(password != null) hashPassword(password.value)
            else rs.getString("password")
            val state = state?.name ?: rs.getString("state")
            connection.prepareStatement("UPDATE users SET username = ?, email = ?, password = ?, state = ? where id = ?").apply {
                setString(1, username)
                setString(2, email)
                setString(3, password)
                setString(4, state)
                setLong(5, userId.toLong())
                executeUpdate()
            }

            UserInfo(
                userId,
                Token(rs.getString("token")),
                Username(username),
                Email(email),
                state
            )

        }
    }

    override fun addUser(username: Username, email: Email, password: Password): User = transaction(jdbc) { connection ->
        val token = generateTokenValue()
        val hashPassword = hashPassword(password.value)
        val state = UserState.NORMAL.name
        val prepared = connection.prepareStatement("INSERT INTO users (token, username, email, password, state) values (?,?,?,?, ?)", Statement.RETURN_GENERATED_KEYS).apply {
            setString(1, token)
            setString(2, username.value)
            setString(3, email.value)
            setString(4, hashPassword)
            setString(5, state)
            executeUpdate()
        }
        val id = getIdStatement(prepared)
        NormalUser(id.toInt(), username, email, password, Token(token), UserState.NORMAL)
    }

    override fun getUserProfileByToken(token: String): UserInfo? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT * FROM users WHERE token = ?").apply {
            setString(1, token)
        }

        prepared.executeQuery().use { rs ->
            if(rs.next()) userResult(rs) else null
        }
    }

    override fun login(username: Username, password: Password): UserInfo? = transaction(jdbc){ connection ->
        val hashPassword = hashPassword(password.value)
        val prepared = connection.prepareStatement("SELECT * FROM USERS WHERE username = ?").apply {
            setString(1, username.value)
        }

        prepared.executeQuery().use { rs ->
            if (rs.next()){
                val actualPassword = rs.getString("password")
                if (hashPassword == actualPassword){
                     return@transaction userResult(rs)
                }
            }
            return@transaction null
        }
    }

    override fun getUsersByName(username: String, skip: Int, limit: Int): List<UserInfo> {
        val prepared = jdbc.connection.prepareStatement("SELECT * FROM users WHERE username LIKE ? LIMIT ? OFFSET ?")
        prepared.setString(1, "$username%")
        prepared.setInt(2, limit)
        prepared.setInt(3, skip)
        return prepared.executeQuery().use { rs ->
            val users = mutableListOf<UserInfo>()
            while (rs.next()) {
                users.add(userResult(rs))
            }
            users
        }
    }
}

private fun userResult(rs: ResultSet): UserInfo {
    return UserInfo(
        rs.getInt("id"),
        Token(rs.getString("token")),
        Username(rs.getString("username")),
        Email(rs.getString("email")),
        rs.getString("state")
    )
}

fun getIdStatement(prepared: PreparedStatement): UInt = if(prepared.generatedKeys.next())
    prepared.generatedKeys.getInt(1).toUInt()
else throw SQLException("Something went wrong obtaining the id generated from the statement.")