package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.api.PlayerData
import net.rebux.jumpandrun.item.ItemRegistry
import net.rebux.jumpandrun.item.impl.MenuItem
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerConnectionListener(private val plugin: Plugin) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.playerData[event.player.uniqueId] = PlayerData()
        event.player.inventory.setItem(4, ItemRegistry.getItemStack(MenuItem.id))
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        plugin.playerData.remove(event.player.uniqueId)
    }
}
