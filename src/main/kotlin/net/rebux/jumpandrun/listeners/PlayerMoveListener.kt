package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.events.ParkourFinishEvent
import net.rebux.jumpandrun.utils.ActionBarUtil.sendActionBar
import net.rebux.jumpandrun.utils.TickFormatter
import org.bukkit.Bukkit
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.MessagesConfig
import net.rebux.jumpandrun.config.ParkourConfig
import net.rebux.jumpandrun.config.SoundsConfig
import net.rebux.jumpandrun.safeTeleport
import net.rebux.jumpandrun.utils.MessageBuilder
import net.rebux.jumpandrun.utils.SoundUtil
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit

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

    if (!data.inParkour) {
      return
    }

    if (!timer.started && event.hasMoved()) {
      timer.start()
      SoundUtil.playSound(SoundsConfig.timerStart, player)
    }

    if (timer.started) {
      timer.tick()
    }

    val (time, unit) = TickFormatter.format(timer.ticks)
    val unitString = if (unit == TimeUnit.SECONDS) MessagesConfig.Timer.Unit.seconds else MessagesConfig.Timer.Unit.minutes
    player.sendActionBar(
      MessageBuilder(MessagesConfig.Timer.bar)
        .values(mapOf(
          "time" to time,
          "unit" to unitString))
        .prefix(false)
        .buildSingle()
    )

    if (player.location.y <= ParkourConfig.resetHeight) {
      player.safeTeleport(data.checkpoint!!)
      SoundUtil.playSound(SoundsConfig.resetHeight, player)
    }

    when (player.location.block.getRelative(BlockFace.DOWN).type) {
      ParkourConfig.Block.reset -> handleReset(player)
      ParkourConfig.Block.checkpoint -> handleCheckpoint(player, blockLocation)
      ParkourConfig.Block.finish -> handleFinish(player, blockLocation)
      else -> {}
    }
  }

  private fun handleReset(player: Player) {
    if (!ParkourConfig.Feature.resetBlock) {
      return
    }

    player.safeTeleport(player.data.checkpoint!!)
    MessageBuilder(MessagesConfig.Event.resetBlock).buildAndSend(player)
    SoundUtil.playSound(SoundsConfig.resetBlock, player)
  }

  // TODO: Not a clean solution with the distance checks
  private fun handleCheckpoint(player: Player, blockLocation: Location) {
    if (!ParkourConfig.Feature.checkpoint) {
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
    MessageBuilder(MessagesConfig.Event.checkpoint).buildAndSend(player)
    SoundUtil.playSound(SoundsConfig.checkpoint, player)
  }

  private fun handleFinish(player: Player, blockLocation: Location) {
    // Make sure start block is not finish block
    if (player.data.parkour!!.location.distance(blockLocation) < 2.0) {
      return
    }

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
