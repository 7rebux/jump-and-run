package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.ParkourConfig
import net.rebux.jumpandrun.events.ParkourJoinEvent
import net.rebux.jumpandrun.item.impl.ResetItem
import net.rebux.jumpandrun.item.impl.HiderItem
import net.rebux.jumpandrun.item.impl.LeaveItem
import net.rebux.jumpandrun.item.impl.RestartItem
import net.rebux.jumpandrun.safeTeleport
import net.rebux.jumpandrun.utils.InventoryCache
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object ParkourJoinListener : Listener {

  @EventHandler
  fun onParkourJoin(event: ParkourJoinEvent) {
    if (event.isCancelled) {
      return
    }

    val player = event.player
    val parkour = event.parkour

    // TODO: Remove this because this should be possible?
    // TODO: Also the menu item must stay in inventory
    if (player.data.inParkour) {
      error("Player ${player.name} tried to join a parkour while already being in a parkour!")
    }

    player.data.apply {
      // We only want to set the previous game mode if the players was not in a parkour before
      if (!this.inParkour) {
        this.previousGameMode = player.gameMode
      }

      this.parkour = parkour
      this.checkpoint = parkour.location
    }

    player.gameMode = GameMode.valueOf(ParkourConfig.gameMode)

    player.saveInventory()
    player.inventory.clear()
    player.addParkourItems()

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
      HiderItem,
      LeaveItem
    ).forEach { item -> item.addToInventory(this) }
  }
}
