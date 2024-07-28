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
import net.rebux.jumpandrun.utils.InventoryCache
import net.rebux.jumpandrun.utils.ScoreboardUtil
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
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

    player.data.apply {
      // We only want to set the previous game mode if the players was not in a parkour before
      if (!this.inParkour) {
        this.previousGameMode = player.gameMode
      }

      this.parkour = parkour
      this.checkpoint = parkour.location
    }

    player.gameMode = GameMode.valueOf(ParkourConfig.gameMode)
    player.foodLevel = MAX_FOOD_LEVEL

    player.saveInventory()
    player.inventory.clear()
    player.addParkourItems()

    player.scoreboard = ScoreboardUtil.createParkourScoreboard(parkour, player)

    if (player.data.playersHidden) {
      Bukkit.getOnlinePlayers().forEach(player::hidePlayer)
    }

    player.safeTeleport(parkour.location)
  }

  private fun Player.saveInventory() {
    InventoryCache.inventories[this] = buildMap {
      val player = this@saveInventory

      for (i in 0..player.inventory.size) {
        if (player.inventory.getItem(i)?.type in listOf(null, Material.AIR)) {
          continue
        }

        this[i] = player.inventory.getItem(i)!!.clone()
      }
    }
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
