package com.crossBoard.repository.jdbc

import java.sql.Connection
import javax.sql.DataSource

/**
 * Auxiliary function to perform a safe transaction.
 * @param jdbc The datasource providing the connection.
 * @param block The block to run safely.
 */
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