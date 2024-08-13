package net.rebux.jumpandrun.parkour

import org.bukkit.Location
import org.bukkit.Material
import java.util.*

data class Parkour(
    val id: Int,
    val name: String,
    val builder: String,
    val difficulty: ParkourDifficulty,
    val material: Material,
    val startLocation: Location,
    val finishLocation: Location? = null
) {
  val times: HashMap<UUID, Long> = hashMapOf()
}
