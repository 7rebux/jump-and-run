package net.rebux.jumpandrun.sql

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

class SQLConnection {
    private lateinit var connection: Connection

    fun connect(hostname: String, port: String, database: String, username: String, password: String) {
        connection = DriverManager.getConnection("jdbc:mysql://$hostname:$port/$database?characterEncoding=utf8", username, password)
    }

    fun disconnect() = connection.close()

    fun hasTable(table: String): Boolean {
        return connection.metaData.getTables(null, null, table, null).next()
    }

    fun update(query: String) {
        val statement: Statement = connection.createStatement()
        statement.executeUpdate(query)
        statement.close()
    }

    fun query(query: String): ResultSet {
        val statement: Statement = connection.createStatement()

        return statement.executeQuery(query)
    }
}