package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.item.Item
import net.rebux.jumpandrun.msgTemplate
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object HiderItem : Item("hider") {

  override fun onInteract(player: Player) {
    if (player.data.playersHidden) {
      Bukkit.getOnlinePlayers().forEach(player::showPlayer)
      player.data.playersHidden = false
      player.msgTemplate("items.hider.disable")
    } else {
      Bukkit.getOnlinePlayers().forEach(player::hidePlayer)
      player.data.playersHidden = true
      player.msgTemplate("items.hider.enable")
    }
  }
}
