package net.rebux.jumpandrun.database.models

import net.rebux.jumpandrun.parkour.Difficulty
import org.bukkit.Material
import org.jetbrains.exposed.dao.id.IntIdTable

object Parkours : IntIdTable() {
    val name = varchar("name", 50)
    val builder = varchar("builder", 50)
    val difficulty = enumerationByName<Difficulty>("difficulty", 10)
    val material = enumerationByName<Material>("material", 50)
    val location = varchar("location", 1024)
}
