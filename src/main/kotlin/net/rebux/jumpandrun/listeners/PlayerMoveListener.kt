package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.*
import net.rebux.jumpandrun.utils.TimeUtil
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class PlayerMoveListener(private val plugin: Plugin) : Listener {

    // This event is manipulated through a custom spigot jar file to be called every tick,
    // regardless if the player moved or not.
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player
        val data = event.player.data
        val timer = data.timer
        val block = player.location.block.location
        // TODO: This is very inaccurate
        val blockLocation = block.add(
            if (block.x < 0) -0.5 else 0.5,
            0.0,
            if (block.z < 0) -0.5 else 0.5
        )

        if (data.parkour == null) {
            return
        }

        if (!timer.started && event.hasMoved()) {
            timer.start()
            player.playSound(player.location, Sound.NOTE_PLING, 1.0F, 1.0F)
        }

        if (timer.started) {
            timer.tick()
        }

        player.sendActionBar(template("timer.bar", mapOf("time" to TimeUtil.formatTicks(timer.ticks))))

        if (player.location.y <= plugin.config.getInt("resetHeight")) {
            player.teleport(data.checkpoint!!)
        }

        when (player.location.block.getRelative(BlockFace.DOWN).type) {
            // Reset
            Material.REDSTONE_BLOCK -> {
                player.teleport(data.checkpoint)
                player.msgTemplate("parkour.resetBlock")
                player.playSound(player.location, Sound.SPIDER_DEATH, 1.0F, 1.0F)
            }
            // Checkpoint
            Material.IRON_BLOCK -> {
                if (data.checkpoint!!.block.location != blockLocation.block.location) {
                    blockLocation.yaw = player.location.yaw
                    blockLocation.pitch = player.location.pitch
                    data.checkpoint = blockLocation

                    player.msgTemplate("parkour.checkpoint")
                    player.playSound(player.location, Sound.ORB_PICKUP, 1.0F, 1.0F)
                }
            }
            // Finish
            Material.EMERALD_BLOCK -> data.parkour!!.finish(player)
            else -> {}
        }
    }
}
