package net.rebux.jumpandrun.api

import org.bukkit.block.Block

data class ParkourSplit(val block: Block) {
    val location = block.location

    var bestTime: Long? = null
    var reached = false
}
