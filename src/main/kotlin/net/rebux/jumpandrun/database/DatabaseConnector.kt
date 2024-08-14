package net.rebux.jumpandrun.database

import net.rebux.jumpandrun.config.DatabaseConfig
import org.jetbrains.exposed.sql.Database

object DatabaseConnector {

    fun connect() {
        Database.connect(
            "jdbc:mariadb://${DatabaseConfig.hostname}:${DatabaseConfig.port}/${DatabaseConfig.database}?characterEncoding=utf8",
            user = DatabaseConfig.username ?: "root",
            password = DatabaseConfig.password ?: "")
    }
}
