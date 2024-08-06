package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.getTag
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent

object PlayerDropItemListener : Listener {

  @EventHandler
  fun onPlayerDropItem(event: PlayerDropItemEvent) {
    val item = event.itemDrop.itemStack

    if (item.type == Material.AIR || item.amount == 0) {
      return
    }

    val idTag = item.getTag(Plugin.ID_TAG)

    idTag?.let {
      event.isCancelled = true
    }
  }
}