package net.rebux.jumpandrun.database.models

import net.rebux.jumpandrun.parkour.ParkourDifficulty
import org.bukkit.Material
import org.jetbrains.exposed.dao.id.IntIdTable

object Parkours : IntIdTable() {

    val name        = text("name")
    val builder     = text("builder")
    val difficulty  = enumerationByName<ParkourDifficulty>("difficulty", 10)
    val material    = enumerationByName<Material>("material", 50)
    val location    = reference("location", Locations)
}
