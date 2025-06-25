package com.crossBoard.repository.jdbc

import com.crossBoard.domain.Email
import com.crossBoard.domain.NormalUser
import com.crossBoard.domain.Password
import com.crossBoard.domain.Token
import com.crossBoard.domain.User
import com.crossBoard.domain.UserInfo
import com.crossBoard.domain.UserState
import com.crossBoard.domain.Username
import com.crossBoard.repository.interfaces.UserRepository
import com.crossBoard.repository.interfaces.generateTokenValue
import com.crossBoard.repository.interfaces.hashPassword
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource

/**
 * Class "JdbcUserRepo" responsible for providing the multiple transactions with the database for the table user.
 * @param jdbc The datasource that provides a connection with the database.
 */
class JdbcUserRepo(private val jdbc: DataSource): UserRepository {

    /**
     * Function "getUserProfileById" responsible to get the user information when searched by his id.
     * @param userId the id of the user being searched.
     * @return UserProfileInfo? the user profile information if found, null otherwise.
     */
    override fun getUserProfileById(userId:Int): UserInfo? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT * FROM users WHERE id = ?").apply {
            setLong(1, userId.toLong())
        }

        prepared.executeQuery().use { rs ->
            if(rs.next()) userResult(rs) else null
        }
    }

    /**
     * Function "getUserProfileByEmail" responsible to get the user information when searched by his email.
     * @param email the email of the user being searched.
     * @return UserProfileInfo? the user profile information if found, null otherwise.
     */
    override fun getUserProfileByEmail(email: Email): UserInfo? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT * FROM users WHERE email = ?").apply {
            setString(1, email.value)
        }

        prepared.executeQuery().use { rs ->
            if(rs.next()) userResult(rs) else null
        }
    }

    /**
     * Function "getUserProfileByName" responsible to get the user information when searched by his username.
     * @param username the username of the user being searched.
     * @return UserProfileInfo? the user profile information if found, null otherwise.
     */
    override fun getUserProfileByName(username: Username): UserInfo? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT * FROM users WHERE username = ?").apply {
            setString(1, username.value)
        }

        prepared.executeQuery().use { rs ->
            if(rs.next()) userResult(rs) else null
        }
    }

    /**
     * Function "deleteUser" responsible to delete the user from the list of users.
     * @param userId the id of the user being deleted.
     * @return Boolean true if the user was deleted, false otherwise.
     */
    override fun deleteUser(userId: Int): Boolean = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("DELETE FROM users WHERE id = ?").apply {
            setLong(1, userId.toLong())
            executeUpdate()
        }
        true
    }

    /**
     * Function "updateUser" responsible to update the user information.
     * @param userId the id of the user being updated.
     * @param username the new username of the user.
     * @param email the new email of the user.
     * @param password the new password of the user.
     * @return UserProfileInfo the user profile information of the updated user.
     */
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

    /**
     * Function "addUser" responsible to add a new user to the list of users.
     * @param username the username of the new user.
     * @param email the email of the new user.
     * @param password the password of the new user.
     * @return UserProfileInfo the user profile information of the new user.
     */
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

    /**
     * Responsible for getting a user given a token.
     * @param token The token string of the user to find.
     */
    override fun getUserProfileByToken(token: String): UserInfo? = transaction(jdbc) { connection ->
        val prepared = connection.prepareStatement("SELECT * FROM users WHERE token = ?").apply {
            setString(1, token)
        }

        prepared.executeQuery().use { rs ->
            if(rs.next()) userResult(rs) else null
        }
    }

    /**
     * Responsible for performing the login of a user.
     * @param username The username of the user to log in to.
     * @param password The password of the user to log in to.
     */
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

    /**
     * Responsible for getting the users that match a given username sequence.
     * @param username The username sequence to get matches of.
     * @param skip The number of elements to skip.
     * @param limit The maximum number of elements to get.
     */
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

/**
 * Private auxiliary function responsible for converting a result set from the table user into UserInfo type.
 * @param rs The user result set.
 */
private fun userResult(rs: ResultSet): UserInfo {
    return UserInfo(
        rs.getInt("id"),
        Token(rs.getString("token")),
        Username(rs.getString("username")),
        Email(rs.getString("email")),
        rs.getString("state")
    )
}

/**
 * Auxiliary function that extracts the generated id of a statement.
 * @param prepared The statement to extract the id of.
 * @throws SQLException When there are no generated keys in the statement.
 */
fun getIdStatement(prepared: PreparedStatement): UInt = if(prepared.generatedKeys.next())
    prepared.generatedKeys.getInt(1).toUInt()
else throw SQLException("Something went wrong obtaining the id generated from the statement.")