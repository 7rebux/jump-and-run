package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager.data
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent

object FoodLevelChangeListener : Listener {

    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        (event.entity as? Player)?.run {
            if (this.data.inParkour || this.data.inPractice) {
                event.isCancelled = true
            }
        }
    }
}
