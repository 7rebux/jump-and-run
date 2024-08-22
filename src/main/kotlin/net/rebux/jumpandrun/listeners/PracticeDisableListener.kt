package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.MessagesConfig
import net.rebux.jumpandrun.events.PracticeDisableEvent
import net.rebux.jumpandrun.safeTeleport
import net.rebux.jumpandrun.utils.EventLogger
import net.rebux.jumpandrun.utils.MessageBuilder
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object PracticeDisableListener : Listener {

    @EventHandler
    fun onDisable(event: PracticeDisableEvent) {
        if (event.isCancelled) {
            return
        }

        val player = event.player
        val practiceData = player.data.practiceData

        player.safeTeleport(practiceData.startLocation!!)
        practiceData.apply {
            timer.stop()
            startLocation = null
        }

        practiceData.previousState!!.restore()

        MessageBuilder(MessagesConfig.Command.Practice.disabled).buildAndSend(player)

        EventLogger.log(
            "PracticeDisableEvent",
            "Player ${player.name} disabled practice mode (inParkour=${player.data.inParkour})"
        )
    }
}
