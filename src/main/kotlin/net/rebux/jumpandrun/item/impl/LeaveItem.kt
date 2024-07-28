package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.events.ParkourLeaveEvent
import net.rebux.jumpandrun.item.Item
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object LeaveItem : Item("leave") {

  override fun onInteract(player: Player) {
    if (!player.data.inParkour) {
      return
    }

    Bukkit.getPluginManager().callEvent(ParkourLeaveEvent(player))
  }
}
