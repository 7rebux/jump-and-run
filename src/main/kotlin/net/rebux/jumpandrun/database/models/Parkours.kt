package net.rebux.jumpandrun.database.models

import net.rebux.jumpandrun.parkour.ParkourDifficulty
import org.bukkit.Material
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Parkours : IntIdTable() {

    val name = text("name")
    val builder = text("builder")
    val difficulty = enumerationByName<ParkourDifficulty>("difficulty", 10)
    val material = enumerationByName<Material>("material", 50)
    val location = reference("location", Locations, onDelete = ReferenceOption.CASCADE)
    val finishLocation = reference("finishLocation", Locations, onDelete = ReferenceOption.CASCADE).nullable()
}
