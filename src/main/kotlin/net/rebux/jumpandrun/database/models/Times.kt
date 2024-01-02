package net.rebux.jumpandrun.database.models

import net.rebux.jumpandrun.parkour.MinecraftVersion
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object Times : IntIdTable() {
    val uuid = uuid("uuid")
    val time = integer("time")
    val version = enumerationByName<MinecraftVersion>("version", 50)
    val date = datetime("date")
    val parkour = reference("parkour", Parkours)
}
