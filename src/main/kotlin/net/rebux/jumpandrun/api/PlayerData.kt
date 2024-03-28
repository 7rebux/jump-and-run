package net.rebux.jumpandrun.api

import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.utils.TickCounter
import org.bukkit.Location
import java.util.UUID

data class PlayerData(
    val uuid: UUID,
    var parkour: Parkour? = null,
    var checkpoint: Location? = null,
    var timer: TickCounter = TickCounter(),
)
