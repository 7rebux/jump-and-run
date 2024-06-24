package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.events.ParkourJoinEvent
import net.rebux.jumpandrun.item.impl.ResetItem
import net.rebux.jumpandrun.item.impl.HiderItem
import net.rebux.jumpandrun.item.impl.LeaveItem
import net.rebux.jumpandrun.item.impl.RestartItem
import net.rebux.jumpandrun.safeTeleport
import net.rebux.jumpandrun.utils.InventoryCache
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ParkourJoinListener : Listener {

  @EventHandler
  fun onParkourJoin(event: ParkourJoinEvent) {
    if (event.isCancelled) {
      return
    }

    val player = event.player

    if (player.data.isInParkour()) {
      error("Player ${player.name} tried to join a parkour while already being in a parkour!")
    }

    player.gameMode = GameMode.ADVENTURE
    player.safeTeleport(event.parkour.location)
    player.data.apply {
      this.parkour = event.parkour
      this.checkpoint = event.parkour.location
    }

    InventoryCache.saveInventory(player)
    player.inventory.clear()
    addParkourItems(player)

    if (player.data.playersHidden) {
      Bukkit.getOnlinePlayers().forEach(player::hidePlayer)
    }
  }

  private fun addParkourItems(player: Player) {
    listOf(
      ResetItem,
      RestartItem,
      HiderItem,
      LeaveItem
    ).forEach { item -> item.addToInventory(player) }
  }
}
