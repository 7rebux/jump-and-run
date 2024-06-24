package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.events.ParkourJoinEvent
import net.rebux.jumpandrun.item.ItemRegistry
import net.rebux.jumpandrun.item.impl.CheckpointItem
import net.rebux.jumpandrun.item.impl.LeaveItem
import net.rebux.jumpandrun.item.impl.RestartItem
import net.rebux.jumpandrun.safeTeleport
import net.rebux.jumpandrun.utils.InventoryCache
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ParkourJoinListener : Listener {

  @EventHandler
  fun onParkourJoin(event: ParkourJoinEvent) {
    if (event.isCancelled) {
      return
    }

    val player = event.player

    assert(
      !player.data.isInParkour(),
      error("Player ${player.name} tried to join a parkour while already being in a parkour!")
    )

    player.gameMode = GameMode.ADVENTURE
    player.safeTeleport(event.parkour.location)
    player.data.apply {
      this.parkour = event.parkour
      this.checkpoint = event.parkour.location
    }

    // TODO: Config entries for inventory items
    InventoryCache.saveInventory(player)
    player.inventory.clear()
    player.inventory.setItem(0, ItemRegistry.getItemStack(CheckpointItem.id))
    player.inventory.setItem(1, ItemRegistry.getItemStack(RestartItem.id))
    player.inventory.setItem(8, ItemRegistry.getItemStack(LeaveItem.id))
  }
}
