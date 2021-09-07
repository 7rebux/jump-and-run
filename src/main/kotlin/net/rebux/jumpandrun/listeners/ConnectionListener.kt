package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Main
import net.rebux.jumpandrun.parkour.Timer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class ConnectionListener: Listener {
    @EventHandler
    fun onConnect(event: PlayerJoinEvent) {
        Main.instance.timers[event.player] = Timer(event.player)
    }

    @EventHandler
    fun onDisconnect(event: PlayerQuitEvent) {
        Main.instance.timers.remove(event.player)
    }
}