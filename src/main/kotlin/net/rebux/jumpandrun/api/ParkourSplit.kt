package net.rebux.jumpandrun.api

import org.bukkit.block.Block

data class ParkourSplit(
    val block: Block,
    var bestTime: Long? = null,
) {
    val location = block.location
}
