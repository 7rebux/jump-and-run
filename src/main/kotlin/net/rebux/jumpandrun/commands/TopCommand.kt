package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.msgTemplate
import net.rebux.jumpandrun.utils.TimeUtil
import org.bukkit.Bukkit
import org.bukkit.ChatColor
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

                if (parkour.times.isEmpty()) {
                    sender.msgTemplate("commands.top.empty")
                    return true
                }

                val bestTime = parkour.times.minOf { it.value }

                sender.msgTemplate("commands.top.header", mapOf("name" to parkour.name))

                parkour.times
                    .toList()
                    .groupBy { it.second }
                    .toSortedMap()
                    .toList()
                    .take(5)
                    .toMap()
                    .asIterable()
                    .forEachIndexed { i, (time, records) ->
                        sender.msgTemplate("commands.top.time", mapOf(
                            "rank" to i+1,
                            "player" to records.toMap().keys.joinToString(separator = ", ") { Bukkit.getOfflinePlayer(it).name },
                            "time" to TimeUtil.ticksToTime(time),
                            "delta" to if (time - bestTime == 0) "${ChatColor.GOLD}✫" else "-" + TimeUtil.ticksToTime(time - bestTime)
                        ))
                    }
            }
        }

        return true
    }
}
