package net.rebux.jumpandrun.listeners

import net.minecraft.server.v1_8_R3.MinecraftServer
import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.msgTemplate
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

/**
 * Contains event listeners for [PlayerMoveEvent]
 */
object MovementListener: Listener {

    private val plugin = Instance.plugin

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player
        val block = player.location.block.location
        val blockLocation = block.add(
            if (block.x < 0) -0.5 else 0.5,
            0.0,
            if (block.z < 0) -0.5 else 0.5
        )

        // check if player is doing parkour
        if (player !in plugin.active)
            return

        // start time if not running and player has moved
        if (player !in plugin.times && event.hasMoved())
            plugin.times[player] = MinecraftServer.getServer().at()

        // reset if player is underneath minimum height
        if (player.location.y <= plugin.config.getInt("resetHeight"))
            player.teleport(plugin.checkpoints[player])

        when (player.location.block.getRelative(BlockFace.DOWN).type) {
            // handle reset blocks
            Material.REDSTONE_BLOCK -> {
                player.teleport(plugin.checkpoints[player])
            }

            // handle checkpoints
            Material.IRON_BLOCK -> {
                if (plugin.checkpoints[player]!!.block.location != blockLocation.block.location) {
                    blockLocation.yaw = player.location.yaw
                    blockLocation.pitch = player.location.pitch

                    plugin.checkpoints[player] = blockLocation
                    player.msgTemplate("parkour.checkpoint")
                    player.playSound(player.location, Sound.ORB_PICKUP, 1.0F, 1.0F)
                }
            }

            // handle finish
            Material.EMERALD_BLOCK -> {
                plugin.active[player]!!.finish(player)
            }

            else -> {}
        }
    }

    private fun PlayerMoveEvent.hasMoved(): Boolean {
        return this.from.x != this.to.x || this.from.y != this.to.y || this.from.z != this.to.z
    }
}
