package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.ParkourConfig
import net.rebux.jumpandrun.events.ParkourLeaveEvent
import net.rebux.jumpandrun.utils.InventoryCache
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

    player.gameMode = player.data.previousGameMode!!

    player.data.apply {
      this.parkour = null
      this.checkpoint = null
      this.previousGameMode = null
      this.timer.stop()
    }

    player.scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard

    // Load pre parkour inventory
    player.inventory.clear()
    InventoryCache.inventories.remove(player)?.forEach { (slot, itemStack) ->
      player.inventory.setItem(slot, itemStack)
    }

    Bukkit.getOnlinePlayers().forEach(player::showPlayer)

    if (ParkourConfig.spawnOnLeave) {
      player.performCommand("spawn")
    }
  }
}
