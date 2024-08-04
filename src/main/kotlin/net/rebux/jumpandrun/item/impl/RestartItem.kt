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

    // Prevent restarting parkour when in practice mode
    if (player.data.inPractice) {
      return
    }

    val startLocation = player.data.parkourData.parkour!!.location

    player.data.parkourData.checkpoint = startLocation
    player.data.parkourData.timer.stop()
    player.safeTeleport(startLocation)
  }
}
