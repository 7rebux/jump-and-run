package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Main
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent

class WorldChangeListener: Listener {
    @EventHandler
    fun onWorldChange(event: PlayerChangedWorldEvent) {
        if (Main.instance.playerCheckpoints.containsKey(event.player))
            Main.instance.playerCheckpoints[event.player]!!.first.quit(event.player)
    }
}