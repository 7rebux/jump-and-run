package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.*
import net.rebux.jumpandrun.utils.TimeUtil
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class PlayerMoveListener(private val plugin: Plugin) : Listener {

    // This event is manipulated through a custom spigot jar file to be called every tick,
    // regardless if the player moved or not.
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player
        val block = player.location.block.location
        // TODO: This is very inaccurate
        val blockLocation = block.add(
            if (block.x < 0) -0.5 else 0.5,
            0.0,
            if (block.z < 0) -0.5 else 0.5
        )

        // Handle tick timers
        if (player.data.isInParkour() && !player.data.isInPracticeMode()) {
            processParkourTimer(player, event.hasMoved())
        } else if (player.data.isInPracticeMode()) {
            processPracticeTimer(player, event.hasMoved())
        } else {
            return
        }

        // Handle reset height
        if (player.location.y <= plugin.config.getInt("resetHeight")) {
            player.teleport(player.data.parkourData.checkpoint!!)
        }

        // Handle special blocks
        when (player.location.block.getRelative(BlockFace.DOWN).type) {
            Material.REDSTONE_BLOCK -> handleReset(player)
            Material.IRON_BLOCK -> handleCheckpoint(player, blockLocation)
            Material.EMERALD_BLOCK -> handleFinish(player)
            else -> {}
        }
    }

    private fun processParkourTimer(player: Player, hasMoved: Boolean) {
        val timer = player.data.parkourData.timer

        if (!timer.started && hasMoved) {
            timer.start()
            player.playSound(player.location, Sound.NOTE_PLING, 1.0F, 1.0F)
        } else if (timer.started) {
            timer.tick()
        }

        player.sendActionBar(template("timer.bar", mapOf("time" to TimeUtil.formatTicks(timer.ticks))))
    }

    private fun processPracticeTimer(player: Player, hasMoved: Boolean) {
        val timer = player.data.practiceData.timer

        if (!timer.started && hasMoved) {
            timer.start()
        } else if (timer.started) {
            timer.tick()
        }

        // TODO: Maybe show this in a different color
        player.sendActionBar(template("timer.bar", mapOf("time" to TimeUtil.formatTicks(timer.ticks))))
    }

    private fun handleReset(player: Player) {
        player.teleport(player.data.parkourData.checkpoint)
        player.msgTemplate("parkour.resetBlock")
        player.playSound(player.location, Sound.SPIDER_DEATH, 1.0F, 1.0F)
    }

    private fun handleCheckpoint(player: Player, blockLocation: Location) {
        // We ignore checkpoints in practice mode
        if (player.data.isInPracticeMode()) {
            return
        }

        if (player.data.parkourData.checkpoint!!.block.location != blockLocation.block.location) {
            blockLocation.yaw = player.location.yaw
            blockLocation.pitch = player.location.pitch
            player.data.parkourData.checkpoint = blockLocation

            player.msgTemplate("parkour.checkpoint")
            player.playSound(player.location, Sound.ORB_PICKUP, 1.0F, 1.0F)
        }
    }

    private fun handleFinish(player: Player) {
        // We don't want to finish a parkour in practice mode
        if (player.data.isInPracticeMode()) {
            return
        }

        player.data.parkourData.parkour!!.finish(player)
    }
}
