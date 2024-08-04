package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.api.currentState
import net.rebux.jumpandrun.config.ParkourConfig
import net.rebux.jumpandrun.events.ParkourJoinEvent
import net.rebux.jumpandrun.item.impl.ResetItem
import net.rebux.jumpandrun.item.impl.HiderItem
import net.rebux.jumpandrun.item.impl.LeaveItem
import net.rebux.jumpandrun.item.impl.MenuItem
import net.rebux.jumpandrun.item.impl.RestartItem
import net.rebux.jumpandrun.safeTeleport
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

    if (!player.data.inParkour) {
      player.data.parkourData.previousState = player.currentState()
      player.inventory.clear()
      player.addParkourItems()
    }

    player.data.apply {
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
