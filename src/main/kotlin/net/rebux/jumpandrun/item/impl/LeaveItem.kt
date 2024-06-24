package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.item.Item
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object LeaveItem : Item("leave") {

  override fun onInteract(player: Player) {
    player.performCommand("spawn")
    Bukkit.getPluginManager().callEvent(PlayerCommandPreprocessEvent(player, "/spawn"))
  }
}
