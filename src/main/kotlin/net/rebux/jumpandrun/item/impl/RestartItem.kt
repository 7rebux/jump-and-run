package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.item.Item
import net.rebux.jumpandrun.safeTeleport
import org.bukkit.entity.Player

object RestartItem : Item("restart") {

  override fun onInteract(player: Player) {
    if (!player.data.inParkour) {
      return
    }

    val startLocation = player.data.parkour!!.location

    player.data.checkpoint = startLocation
    player.data.timer.stop()
    player.safeTeleport(startLocation)
  }
}
