package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.data
import net.rebux.jumpandrun.utils.InventoryUtil
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class CommandPreprocessListener : Listener {

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        if (event.message != "/spawn" || event.player.data.parkour == null) {
            return
        }

        event.player.data.apply {
            this.parkour = null
            this.checkpoint = null
            this.timer.stop()
        }

        event.player.gameMode = GameMode.SURVIVAL
        InventoryUtil.loadInventory(event.player)
    }
}