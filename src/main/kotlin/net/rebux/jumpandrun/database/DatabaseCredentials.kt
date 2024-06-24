package net.rebux.jumpandrun.database

data class DatabaseCredentials(
  val host: String,
  val port: String,
  val name: String,
  val user: String?,
  val pass: String?
)
