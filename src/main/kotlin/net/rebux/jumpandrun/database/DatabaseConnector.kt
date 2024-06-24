package net.rebux.jumpandrun.database

import net.rebux.jumpandrun.Plugin
import org.jetbrains.exposed.sql.Database

class DatabaseConnector(private val plugin: Plugin) {

  private val credentials: DatabaseCredentials = DatabaseCredentials(
    plugin.config.getString("database.host")!!,
    plugin.config.getString("database.port")!!,
    plugin.config.getString("database.name")!!,
    plugin.config.getString("database.user"),
    plugin.config.getString("database.pass")
  )

  fun connect() {
    Database.connect(
      "jdbc:mariadb://${credentials.host}:${credentials.port}/${credentials.name}?characterEncoding=utf8",
      user = credentials.user ?: "root",
      password = credentials.pass ?: ""
    )
  }
}
