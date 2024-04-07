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
        }

        if (timer.started) {
            timer.tick()
        }

        player.sendActionBar(template("timer.bar", mapOf("time" to TimeUtil.ticksToTime(timer.ticks))))

        if (player.location.y <= plugin.config.getInt("resetHeight")) {
            player.teleport(data.checkpoint!!)
        }

        when (player.location.block.getRelative(BlockFace.DOWN).type) {
            // TODO: Send message to the player
            Material.REDSTONE_BLOCK -> player.teleport(data.checkpoint)
            Material.IRON_BLOCK -> {
                if (data.checkpoint!!.block.location != blockLocation.block.location) {
                    blockLocation.yaw = player.location.yaw
                    blockLocation.pitch = player.location.pitch
                    data.checkpoint = blockLocation

                    player.msgTemplate("parkour.checkpoint")
                    player.playSound(player.location, Sound.ORB_PICKUP, 1.0F, 1.0F)
                }
            }
            Material.EMERALD_BLOCK -> data.parkour!!.finish(player)
            else -> {}
        }
    }
}
