package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.events.ParkourLeaveEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object CommandPreprocessListener : Listener {

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        if (event.message != "/spawn" || !event.player.data.inParkour) {
            return
        }

        Bukkit.getPluginManager()
            .callEvent(ParkourLeaveEvent(event.player, preventSpawnTeleport = true))
    }
}
