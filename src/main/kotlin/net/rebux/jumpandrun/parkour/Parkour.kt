package net.rebux.jumpandrun.parkour

import java.util.*
import org.bukkit.Location
import org.bukkit.Material

data class Parkour(
    val id: Int,
    val name: String,
    val builder: String,
    val difficulty: ParkourDifficulty,
    val material: Material,
    val location: Location,
) {
  val times: HashMap<UUID, Long> = hashMapOf()
}
