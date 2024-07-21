package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager.data
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

object EntityDamageListener : Listener {

  @EventHandler
  fun onEntityDamage(event: EntityDamageEvent) {
    (event.entity as? Player)?.run {
      if (this.data.inParkour) {
        event.isCancelled = true
      }
    }
  }
}
