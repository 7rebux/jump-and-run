package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.api.currentState
import net.rebux.jumpandrun.config.MessagesConfig
import net.rebux.jumpandrun.config.ParkourConfig
import net.rebux.jumpandrun.events.PracticeEnableEvent
import net.rebux.jumpandrun.item.ItemRegistry
import net.rebux.jumpandrun.item.impl.PracticeItem
import net.rebux.jumpandrun.utils.MessageBuilder
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.util.NumberConversions

object PracticeEnableListener : Listener {

    @EventHandler
    fun onEnable(event: PracticeEnableEvent) {
        if (event.isCancelled) {
            return
        }

        val player = event.player
        val practiceData = player.data.practiceData

        practiceData.startLocation = player.location

        // Pause parkour timer when player is in a parkour
        if (player.data.inParkour) {
            player.data.parkourData.timer.pause()
        }

        player.data.practiceData.previousState = player.currentState()
        player.inventory.clear()
        player.inventory.setItem(0, ItemRegistry.getItemStack(PracticeItem.id))

        player.gameMode = GameMode.valueOf(ParkourConfig.gameMode)

        MessageBuilder(MessagesConfig.Command.Practice.enabled)
            .values(
                mapOf(
                    "x" to "%.3f".format(player.location.x),
                    "y" to "%.3f".format(player.location.y),
                    "z" to "%.3f".format(player.location.z),
                    "direction" to player.location.facing(),
                    "yaw" to "%.3f".format(player.location.yaw),
                    "pitch" to "%.3f".format(player.location.pitch)))
            .buildAndSend(player)
    }

    private fun Location.facing(): String {
        return when (NumberConversions.floor((this.yaw * 4.0F / 360.0F).toDouble() + 0.5) and 3) {
            0 -> "Z+"
            1 -> "X-"
            2 -> "Z-"
            else -> "X+"
        }
    }
}
