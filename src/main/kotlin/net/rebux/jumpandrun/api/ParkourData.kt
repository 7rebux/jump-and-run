package net.rebux.jumpandrun.api

import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.utils.TickCounter
import org.bukkit.Location

data class ParkourData(
    var previousState: PlayerStatePersistence? = null,
    var parkour: Parkour? = null,
    var checkpoint: Location? = null,
    var timer: TickCounter = TickCounter(),
    val splits: MutableList<ParkourSplit> = mutableListOf()
)
