package net.rebux.jumpandrun.parkour

import org.bukkit.*
import java.util.*

data class Parkour(
  val id: Int,
  val name: String,
  val builder: String,
  val difficulty: ParkourDifficulty,
  val material: Material,
  val location: Location,
  val times: HashMap<UUID, Long> = hashMapOf()
)
