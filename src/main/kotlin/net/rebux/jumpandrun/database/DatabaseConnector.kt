package net.rebux.jumpandrun.database

import net.rebux.jumpandrun.config.DatabaseConfig
import org.jetbrains.exposed.sql.Database

object DatabaseConnector {

  private val credentials: DatabaseCredentials = DatabaseCredentials(
    DatabaseConfig.hostname,
    DatabaseConfig.port,
    DatabaseConfig.database,
    DatabaseConfig.username,
    DatabaseConfig.password
  )

  fun connect() {
    Database.connect(
      "jdbc:mariadb://${credentials.host}:${credentials.port}/${credentials.name}?characterEncoding=utf8",
      user = credentials.user ?: "root",
      password = credentials.pass ?: ""
    )
  }
}
