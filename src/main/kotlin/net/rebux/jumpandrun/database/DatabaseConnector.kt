package net.rebux.jumpandrun.database

import net.rebux.jumpandrun.Instance
import org.jetbrains.exposed.sql.Database

class DatabaseConnector {

    private val credentials: DatabaseCredentials
    private val plugin = Instance.plugin

    init {
        credentials = DatabaseCredentials(
            plugin.config.getString("database.host"),
            plugin.config.getString("database.port"),
            plugin.config.getString("database.name"),
            plugin.config.getString("database.user"),
            plugin.config.getString("database.pass")
        )
    }

    fun connect() {
        Database.connect(
            "jdbc:mariadb://${credentials.host}:${credentials.port}/${credentials.name}?characterEncoding=utf8",
            user = credentials.user ?: "root",
            password = credentials.pass ?: ""
        )
    }
}
