package net.rebux.jumpandrun.api

import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.utils.TickCounter
import org.bukkit.Location

data class PlayerData(
    var parkour: Parkour? = null,
    var checkpoint: Location? = null,
    var timer: TickCounter = TickCounter(),
) {

    fun isInParkour() = parkour != null
}
