package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Main
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class MovementListener: Listener {
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (event.player !in Main.instance.playerCheckpoints)
            return

        if (event.player.location.y <= Main.instance.playerCheckpoints[event.player]!!.first.resetHeight)
            event.player.teleport(Main.instance.playerCheckpoints[event.player]!!.second)

        when (event.player.location.block.getRelative(BlockFace.DOWN).type) {
            Material.EMERALD_BLOCK -> {
                Main.instance.playerCheckpoints[event.player]!!.first.finish(event.player)
            }
            Material.IRON_BLOCK -> {
                if (Main.instance.playerCheckpoints[event.player]!!.second.distanceSquared(event.player.location) > 2) {
                    Main.instance.playerCheckpoints[event.player] = Pair(Main.instance.playerCheckpoints[event.player]!!.first, event.player.location)
                    event.player.sendMessage("${Main.PREFIX} Du hast einen neuen Checkpoint erreicht!")
                    event.player.playSound(event.player.location, Sound.ORB_PICKUP, 1.0F, 1.0F)
                }
            }
            Material.REDSTONE_BLOCK -> {
                event.player.teleport(Main.instance.playerCheckpoints[event.player]!!.second)
            }
            else -> {}
        }
    }
}
