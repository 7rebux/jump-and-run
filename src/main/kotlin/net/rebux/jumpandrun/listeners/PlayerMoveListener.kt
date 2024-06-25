package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.events.ParkourFinishEvent
import net.rebux.jumpandrun.utils.ActionBarUtil.sendActionBar
import net.rebux.jumpandrun.utils.TickFormatter
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.MessagesConfig
import net.rebux.jumpandrun.config.ParkourConfig
import net.rebux.jumpandrun.safeTeleport
import net.rebux.jumpandrun.utils.MessageBuilder
import org.bukkit.Location
import org.bukkit.entity.Player

object PlayerMoveListener : Listener {

  // This event is manipulated through a custom server jar file to be called every tick,
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

    if (!data.isInParkour()) {
      return
    }

    if (!timer.started && event.hasMoved()) {
      timer.start()
      // TODO: Maybe put this in config?
      player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F)
    }

    if (timer.started) {
      timer.tick()
    }

    // TODO: Put action bar string in config
    val (time, unit) = TickFormatter.format(timer.ticks)
    player.sendActionBar("$time $unit")

    if (player.location.y <= ParkourConfig.resetHeight) {
      player.safeTeleport(data.checkpoint!!)
      player.playSound(player.location, Sound.ENTITY_SPIDER_DEATH, 1.0F, 1.0F) // TODO: Maybe put this in config?
    }

    when (player.location.block.getRelative(BlockFace.DOWN).type) {
      ParkourConfig.Block.reset -> handleReset(player)
      ParkourConfig.Block.checkpoint -> handleCheckpoint(player, blockLocation)
      ParkourConfig.Block.finish -> handleFinish(player)
      else -> {}
    }
  }

  private fun handleReset(player: Player) {
    if (!ParkourConfig.Feature.resetBlock) {
      return
    }

    player.safeTeleport(player.data.checkpoint!!)
    MessageBuilder()
      .template(MessagesConfig.Event.resetBlock)
      .buildAndSend(player)
    player.playSound(player.location, Sound.ENTITY_SPIDER_DEATH, 1.0F, 1.0F) // TODO: Maybe put this in config?
  }

  // TODO: Not a clean solution with the distance checks
  private fun handleCheckpoint(player: Player, blockLocation: Location) {
    if (!ParkourConfig.Feature.checkpoint) {
      return
    }

    // Make sure start block can not be a checkpoint
    if (player.data.parkour!!.location.distance(blockLocation) < 2.0) {
      return
    }

    // Make sure checkpoint is not already set
    if (player.data.checkpoint!!.distance(blockLocation) < 2.0) {
      return
    }

    player.data.checkpoint = blockLocation.apply {
      this.yaw = player.location.yaw
      this.pitch = player.location.pitch
    }
    MessageBuilder()
      .template(MessagesConfig.Event.checkpoint)
      .buildAndSend(player)
    player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F) // TODO: Maybe put this in config?
  }

  private fun handleFinish(player: Player) {
    Bukkit.getPluginManager().callEvent(
      ParkourFinishEvent(player, player.data.parkour!!)
    )
  }

  private fun PlayerMoveEvent.hasMoved(): Boolean {
    if (this.to == null) {
      return false
    }

    return this.from.x != this.to!!.x
      || this.from.y != this.to!!.y
      || this.from.z != this.to!!.z
  }
}
