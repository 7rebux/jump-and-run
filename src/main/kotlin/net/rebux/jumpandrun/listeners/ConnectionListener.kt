package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.item.ItemRegistry
import net.rebux.jumpandrun.item.impl.MenuItem
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Contains event listeners for [PlayerJoinEvent] and [PlayerQuitEvent]
 */
object ConnectionListener: Listener {

    private val plugin = Instance.plugin

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        // give menu item
        event.player.inventory.setItem(4, ItemRegistry.getItemStack(MenuItem.id))
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player

        // remove player
        plugin.active.remove(player)
        plugin.checkpoints.remove(player)
        plugin.tickCounters.remove(player)
    }
}
