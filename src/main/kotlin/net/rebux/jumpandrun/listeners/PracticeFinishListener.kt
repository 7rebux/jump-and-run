package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.MessagesConfig
import net.rebux.jumpandrun.events.PracticeFinishEvent
import net.rebux.jumpandrun.safeTeleport
import net.rebux.jumpandrun.utils.MessageBuilder
import net.rebux.jumpandrun.utils.TickFormatter
import net.rebux.jumpandrun.utils.TickFormatter.toMessageValue
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object PracticeFinishListener : Listener {

    @EventHandler
    fun onPracticeFinish(event: PracticeFinishEvent) {
        if (event.isCancelled) {
            return
        }

        val player = event.player
        val ticks = player.data.practiceData.timer.stop()
        val (time, unit) = TickFormatter.format(ticks)

        player.safeTeleport(player.data.practiceData.startLocation!!)
        MessageBuilder(MessagesConfig.Command.Practice.finish)
            .values(
                mapOf(
                    "time" to time,
                    "unit" to unit.toMessageValue(),
                )
            )
            .buildAndSend(player)
    }
}
