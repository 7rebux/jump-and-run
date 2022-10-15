package net.rebux.jumpandrun.sql

import net.rebux.jumpandrun.Instance
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

class SQLConnection {

    private val plugin = Instance.plugin
    private lateinit var connection: Connection

    fun connect() {
        val host = plugin.config.getString("database.host")
        val port = plugin.config.getString("database.port")
        val name = plugin.config.getString("database.name")
        val user = plugin.config.getString("database.user")
        val pass = plugin.config.getString("database.pass")

        connection = DriverManager.getConnection(
            "jdbc:mysql://$host:$port/$name?characterEncoding=utf8",
            user,
            pass
        )
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
