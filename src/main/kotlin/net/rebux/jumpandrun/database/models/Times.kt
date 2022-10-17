package net.rebux.jumpandrun.database.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object Times : IntIdTable() {
    val uuid = varchar("uuid", 36)
    val time = integer("time")
    val date = datetime("date")
    val parkour = reference("parkour", Parkours)
}
