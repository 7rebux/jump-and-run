package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.ParkourConfig
import net.rebux.jumpandrun.events.ParkourLeaveEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object ParkourLeaveListener : Listener {

  @EventHandler
  fun onLeave(event: ParkourLeaveEvent) {
    if (event.isCancelled) {
      return
    }

    val player = event.player

    player.data.parkourData.previousState!!.restore()

    player.data.apply {
      this.parkourData.parkour = null
      this.parkourData.checkpoint = null
      this.parkourData.previousState = null
      this.parkourData.timer.stop()
    }

    player.scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard

    Bukkit.getOnlinePlayers().forEach(player::showPlayer)

    if (ParkourConfig.spawnOnLeave && !event.preventSpawnTeleport) {
      player.performCommand("spawn")
    }
  }
}
