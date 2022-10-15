package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.utils.InventoryUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object CommandListener : Listener {

    private val plugin = Instance.plugin

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        val player = event.player

        // check if command is /spawn
        if (event.message != "/spawn")
            return

        // check if player is doing parkour
        if (!plugin.active.contains(player))
            return

        plugin.active.remove(player)
        plugin.checkpoints.remove(player)
        plugin.times.remove(player)

        // restore inventory
        InventoryUtil.loadInventory(player)
    }
}
