package net.rebux.jumpandrun.api

import net.rebux.jumpandrun.utils.TickCounter
import org.bukkit.Location

data class PracticeData(
  var startLocation: Location? = null,
  var timer: TickCounter = TickCounter()
)
