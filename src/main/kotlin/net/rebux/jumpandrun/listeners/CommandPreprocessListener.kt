package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.utils.InventoryCache
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object CommandPreprocessListener : Listener {

  @EventHandler
  fun onCommand(event: PlayerCommandPreprocessEvent) {
    if (event.message != "/spawn" || !event.player.data.inParkour) {
      return
    }

    event.player.data.apply {
      this.parkour = null
      this.checkpoint = null
      this.previousGameMode = null
      this.timer.stop()
    }

    event.player.gameMode = event.player.data.previousGameMode!!

    event.player.loadInventory()

    Bukkit.getOnlinePlayers().forEach(event.player::showPlayer)
  }

  private fun Player.loadInventory() {
    this.inventory.clear()
    InventoryCache.inventories.remove(this)?.forEach { (slot, itemStack) ->
      this.inventory.setItem(slot, itemStack)
    }
  }
}
