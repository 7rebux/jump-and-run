package net.rebux.jumpandrun.config

object DatabaseConfig : CustomConfiguration("database.yml") {

  val hostname = config.getString("hostname")
    ?: error("Database hostname must be present")
  val port = config.getString("port")
    ?: error("Database port must be present")
  val database = config.getString("database")
    ?: error("Database name must be present")
  val username: String? = config.getString("username")
  val password: String? = config.getString("password")
}
