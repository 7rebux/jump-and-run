package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.utils.TickFormatter
import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.MessagesConfig
import net.rebux.jumpandrun.utils.MessageBuilder
import net.rebux.jumpandrun.utils.TickFormatter.toMessageValue
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TopCommand : CommandExecutor {

  private val messages = MessagesConfig.Command.Top

  override fun onCommand(
    sender: CommandSender,
    command: Command,
    label: String,
    args: Array<out String>
  ): Boolean {
    if (sender !is Player) {
      sender.sendMessage("This command can only be called as a player!")
      return true
    }

    val parkour = sender.data.parkourData.parkour

    if (parkour == null) {
      MessageBuilder(messages.invalid).error().buildAndSend(sender)
      return true
    }

    val entries = if (args.getOrNull(0) == "all") 100 else 5

    if (parkour.times.isEmpty()) {
      MessageBuilder(messages.empty).error().buildAndSend(sender)
      return true
    }

    val bestTime = parkour.times.values.min()

    MessageBuilder(messages.header)
      .values(mapOf(
        "name" to parkour.name,
        "difficulty" to parkour.difficulty.displayName,
        "amount" to entries))
      .buildAndSend(sender)

    parkour.times.entries
      .groupBy { it.value }
      .toSortedMap()
      .asIterable()
      .take(entries)
      .forEachIndexed { i, (ticks, records) ->
        val holders = records
          .mapNotNull { Bukkit.getOfflinePlayer(it.key).name }
          .joinToString(", ")
        val (time, unit) = TickFormatter.format(ticks)

        MessageBuilder(messages.entry)
          .values(mapOf(
            "rank" to i + 1,
            "player" to holders,
            "time" to time,
            "unit" to unit.toMessageValue(),
            "delta" to formatDelta(ticks, bestTime)))
          .buildAndSend(sender)
      }

    return true
  }

  private fun formatDelta(time: Long, bestTime: Long): String {
    return if (time - bestTime == 0L) {
      "${ChatColor.GOLD}✫"
    } else {
      "-" + TickFormatter.format(time - bestTime).first
    }
  }
}
