package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Main
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

@Suppress("SpellCheckingInspection")
object MovementListener: Listener {

    private val plugin = Main.instance

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
                    player.sendMessage("${Main.PREFIX} Du hast einen neuen ${ChatColor.GREEN}Checkpoint ${ChatColor.GRAY}erreicht!")
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
}
