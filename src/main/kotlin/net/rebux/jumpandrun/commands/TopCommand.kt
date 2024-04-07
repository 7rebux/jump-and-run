package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.data
import net.rebux.jumpandrun.msgTemplate
import net.rebux.jumpandrun.parkour.Parkour
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

        val entries = if (args.getOrNull(0) == "all") 100 else 5
        val parkour = sender.data.parkour

        if (parkour == null) {
            sender.msgTemplate("commands.top.invalid")
            return true
        }

        if (parkour.times.isEmpty()) {
            sender.msgTemplate("commands.top.empty")
            return true
        }

        val bestTime = parkour.times.minOf(Parkour.Time::ticks)

        sender.msgTemplate("commands.top.header", mapOf("name" to parkour.name))
        parkour.times
            .groupBy(Parkour.Time::ticks)
            .toSortedMap()
            .asIterable()
            .take(entries)
            .forEachIndexed { i, (time, records) ->
                sender.msgTemplate("commands.top.time", mapOf(
                    "rank" to i + 1,
                    "player" to records.map(Parkour.Time::uuid).joinToString(", ") { Bukkit.getOfflinePlayer(it).name },
                    "time" to TimeUtil.ticksToTime(time),
                    "delta" to if (time - bestTime == 0) "${ChatColor.GOLD}✫" else "-" + TimeUtil.ticksToTime(time - bestTime)
                ))
            }

        return true
    }
}
