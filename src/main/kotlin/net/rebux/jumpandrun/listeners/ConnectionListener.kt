package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Main
import net.rebux.jumpandrun.item.impl.MenuItem
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object ConnectionListener: Listener {

    private val plugin = Main.instance

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        // give menu item
        event.player.inventory.setItem(4, MenuItem().getItemStack())
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player

        // remove player
        plugin.active.remove(player)
        plugin.checkpoints.remove(player)
        plugin.times.remove(player)
    }
}
