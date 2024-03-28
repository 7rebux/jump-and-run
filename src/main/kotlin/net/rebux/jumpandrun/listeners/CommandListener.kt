package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.data
import net.rebux.jumpandrun.utils.InventoryUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

/**
 * Contains event listeners for [PlayerCommandPreprocessEvent]
 */
object CommandListener : Listener {
    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        if (event.message != "/spawn" || event.player.data.parkour == null)
            return

        event.player.data.apply {
            parkour = null
            checkpoint = null
            timer.stop()
        }

        InventoryUtil.loadInventory(event.player)
    }
}
