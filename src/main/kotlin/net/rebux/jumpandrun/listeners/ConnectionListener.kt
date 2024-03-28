package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.api.PlayerData
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
        event.player.inventory.setItem(4, ItemRegistry.getItemStack(MenuItem.id))
        plugin.players += PlayerData(event.player.uniqueId)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        plugin.players.removeIf { it.uuid == event.player.uniqueId }
    }
}
