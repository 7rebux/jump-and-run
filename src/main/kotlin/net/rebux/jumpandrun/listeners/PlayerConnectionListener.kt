package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager
import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.item.impl.MenuItem
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object PlayerConnectionListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        PlayerDataManager.add(event.player)

        event.player.inventory.clear()
        addLobbyItems(event.player)

        Bukkit.getOnlinePlayers()
            .filter { it.data.inParkour }
            .filter { it.data.playersHidden }
            .forEach { it.hidePlayer(event.player) }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        if (event.player.data.inPractice) {
            event.player.data.practiceData.previousState!!.restore()
        }

        if (event.player.data.inParkour) {
            event.player.data.parkourData.previousState!!.restore()
        }

        PlayerDataManager.remove(event.player)
    }

    private fun addLobbyItems(player: Player) {
        MenuItem.addToInventory(player)
    }
}
