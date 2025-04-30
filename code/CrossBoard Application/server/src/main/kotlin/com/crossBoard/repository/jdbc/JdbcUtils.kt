package com.crossBoard.repository.jdbc

import java.sql.Connection
import javax.sql.DataSource

inline fun <T> transaction(jdbc: DataSource, block: (Connection) -> T): T =
    jdbc.connection.use {conn ->
        try {
            conn.autoCommit = false
            conn.transactionIsolation = Connection.TRANSACTION_SERIALIZABLE
            return block(conn).also { conn.commit() }
        }
        catch (e: Exception){
            conn.rollback()
            throw e
        }
    }