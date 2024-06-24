package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager
import net.rebux.jumpandrun.item.ItemRegistry
import net.rebux.jumpandrun.item.impl.MenuItem
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerConnectionListener : Listener {

  @EventHandler
  fun onJoin(event: PlayerJoinEvent) {
    PlayerDataManager.add(event.player)
    event.player.inventory.setItem(4, ItemRegistry.getItemStack(MenuItem.id))
  }

  @EventHandler
  fun onQuit(event: PlayerQuitEvent) {
    PlayerDataManager.remove(event.player)
  }
}
