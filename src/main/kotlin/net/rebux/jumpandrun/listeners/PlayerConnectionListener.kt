package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager
import net.rebux.jumpandrun.item.impl.MenuItem
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object PlayerConnectionListener : Listener {

  @EventHandler
  fun onJoin(event: PlayerJoinEvent) {
    PlayerDataManager.add(event.player)
    addLobbyItems(event.player)
  }

  @EventHandler
  fun onQuit(event: PlayerQuitEvent) {
    PlayerDataManager.remove(event.player)
  }

  private fun addLobbyItems(player: Player) {
    MenuItem.addToInventory(player)
  }
}
