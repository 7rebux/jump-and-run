package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.msgTemplate
import net.rebux.jumpandrun.utils.TimeUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object TopCommand : CommandExecutor {

    private val plugin = Instance.plugin

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        when (sender) {
            !is Player -> sender.msgTemplate("commands.playersOnly")
            !in plugin.active -> sender.msgTemplate("commands.top.invalid")
            else -> {
                val parkour = plugin.active[sender]!!

                parkour.times
                    .toList()
                    .sortedBy { (_, value) -> value }
                    .take(5)
                    .toMap()
                    .asIterable()
                    .forEachIndexed { i, (key, value) ->
                        sender.msgTemplate("commands.top.entry", mapOf(
                            "rank" to i+1,
                            "player" to key.name,
                            "time" to TimeUtil.ticksToTime(value)
                        ))
                    }
            }
        }

        return true
    }
}