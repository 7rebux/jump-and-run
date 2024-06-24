package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.*
import net.rebux.jumpandrun.events.ParkourFinishEvent
import net.rebux.jumpandrun.utils.ActionBarUtil.sendActionBar
import net.rebux.jumpandrun.utils.TickFormatter
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import net.rebux.jumpandrun.api.PlayerDataManager.data

class PlayerMoveListener(private val plugin: Plugin) : Listener {

  // This event is manipulated through a custom spigot jar file to be called every tick,
  // regardless if the player moved or not.
  @EventHandler
  fun onMove(event: PlayerMoveEvent) {
    val player = event.player
    val data = event.player.data
    val timer = data.timer
    val block = player.location.block.location
    val parkour = data.parkour
    // TODO: This is very inaccurate
    val blockLocation = block.add(
      if (block.x < 0) -0.5 else 0.5,
      0.0,
      if (block.z < 0) -0.5 else 0.5
    )

    if (parkour == null) {
      return
    }

    if (!timer.started && event.hasMoved()) {
      timer.start()
      // TODO: Does this still work in 1.8?
      player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F)
    }

    if (timer.started) {
      timer.tick()
    }

    player.sendActionBar(template("timer.bar", mapOf("time" to TickFormatter.format(timer.ticks))))

    if (player.location.y <= plugin.config.getInt("resetHeight")) {
      player.safeTeleport(data.checkpoint!!)
    }

    // TODO: Config for materials and if certain features are enabled
    when (player.location.block.getRelative(BlockFace.DOWN).type) {
      // Reset
      Material.REDSTONE_BLOCK -> {
        player.safeTeleport(data.checkpoint!!)
        player.msgTemplate("parkour.resetBlock")
        player.playSound(player.location, Sound.ENTITY_SPIDER_DEATH, 1.0F, 1.0F)
      }
      // Checkpoint
      Material.IRON_BLOCK -> {
        if (data.checkpoint!!.block.location != blockLocation.block.location) {
          blockLocation.yaw = player.location.yaw
          blockLocation.pitch = player.location.pitch
          data.checkpoint = blockLocation

          player.msgTemplate("parkour.checkpoint")
          player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F)
        }
      }
      // Finish
      Material.GOLD_BLOCK -> {
        Bukkit.getPluginManager().callEvent(
          ParkourFinishEvent(player, parkour)
        )
      }
      else -> {}
    }
  }
}
