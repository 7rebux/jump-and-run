package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.data
import net.rebux.jumpandrun.msgTemplate
import net.rebux.jumpandrun.utils.TimeUtil
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TopCommand : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.msgTemplate("commands.playersOnly")
            return true
        }

        if (!sender.data.isInParkour()) {
            sender.msgTemplate("commands.top.invalid")
            return true
        }

        val entries = if (args.getOrNull(0) == "all") 100 else 5
        val parkour = sender.data.parkour!!

        if (parkour.times.isEmpty()) {
            sender.msgTemplate("commands.top.empty")
            return true
        }

        val bestTime = parkour.times.values.min()

        sender.msgTemplate("commands.top.header", mapOf("name" to parkour.name))
        parkour.times.entries
            .groupBy { it.value }
            .toSortedMap()
            .asIterable()
            .take(entries)
            .forEachIndexed { i, (time, records) ->
                sender.msgTemplate("commands.top.time", mapOf(
                    "rank" to i + 1,
                    "player" to records.joinToString(", ") { Bukkit.getOfflinePlayer(it.key).name },
                    "time" to TimeUtil.formatTicks(time),
                    "delta" to formatDelta(time, bestTime)
                ))
            }

        return true
    }

    private fun formatDelta(time: Long, bestTime: Long): String {
        return if (time - bestTime == 0L) {
            "${ChatColor.GOLD}✫"
        } else {
            "-" + TimeUtil.formatTicks(time - bestTime)
        }
    }
}
