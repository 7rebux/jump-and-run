package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.ParkourConfig
import net.rebux.jumpandrun.events.ParkourJoinEvent
import net.rebux.jumpandrun.item.impl.ResetItem
import net.rebux.jumpandrun.item.impl.HiderItem
import net.rebux.jumpandrun.item.impl.LeaveItem
import net.rebux.jumpandrun.item.impl.MenuItem
import net.rebux.jumpandrun.item.impl.RestartItem
import net.rebux.jumpandrun.safeTeleport
import net.rebux.jumpandrun.utils.InventoryCache.saveInventory
import net.rebux.jumpandrun.utils.ScoreboardUtil
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object ParkourJoinListener : Listener {

  private const val MAX_FOOD_LEVEL = 20

  @EventHandler
  fun onParkourJoin(event: ParkourJoinEvent) {
    if (event.isCancelled) {
      return
    }

    val player = event.player
    val parkour = event.parkour

    // Only save the inventory when the player was not in parkour mode before
    if (!player.data.inParkour) {
      player.saveInventory()
      player.inventory.clear()
      player.addParkourItems()
    }

    player.data.apply {
      // We only want to set the previous game mode if the players was not in a parkour before
      if (!this.inParkour) {
        this.previousGameMode = player.gameMode
      }

      this.parkourData.timer.stop()
      this.parkourData.parkour = parkour
      this.parkourData.checkpoint = parkour.location
    }

    player.gameMode = GameMode.valueOf(ParkourConfig.gameMode)
    player.foodLevel = MAX_FOOD_LEVEL

    player.scoreboard = ScoreboardUtil.createParkourScoreboard(parkour, player)

    if (player.data.playersHidden) {
      Bukkit.getOnlinePlayers().forEach(player::hidePlayer)
    }

    player.safeTeleport(parkour.location)
  }

  private fun Player.addParkourItems() {
    listOf(
      ResetItem,
      RestartItem,
      MenuItem,
      HiderItem,
      LeaveItem
    ).forEach { item -> item.addToInventory(this) }
  }
}
