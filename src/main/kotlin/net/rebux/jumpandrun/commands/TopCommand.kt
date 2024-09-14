package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.MessagesConfig
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.parkour.ParkourManager
import net.rebux.jumpandrun.utils.MessageBuilder
import net.rebux.jumpandrun.utils.TickFormatter
import net.rebux.jumpandrun.utils.TickFormatter.toMessageValue
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
        args: Array<String>
    ): Boolean {
        val amount = args.getOrNull(1)?.toIntOrNull() ?: 5

        when (args.firstOrNull()?.lowercase()) {
            "help" -> sender.showUsage()
            "global" -> handleGlobalEntries(sender, amount)
            null -> {
                val parkour = (sender as? Player)?.data?.parkourData?.parkour

                if (parkour == null) {
                    handleGlobalEntries(sender, amount)
                } else {
                    handleParkourEntries(sender, parkour, amount)
                }
            }
            else -> {
                val parkour = args.firstOrNull()?.toIntOrNull()?.let { id ->
                    ParkourManager.parkours.values.find { it.id == id }
                }

                if (parkour == null) {
                    MessageBuilder(MessagesConfig.Command.Top.Parkour.notFound)
                        .error()
                        .buildAndSend(sender)
                    return true
                }

                handleParkourEntries(sender, parkour, amount)
            }
        }

        return true
    }

    private fun handleParkourEntries(
        sender: CommandSender,
        parkour: Parkour,
        amount: Int
    ) {
        if (parkour.times.isEmpty()) {
            MessageBuilder(MessagesConfig.Command.Top.Parkour.empty)
                .error()
                .buildAndSend(sender)
            return
        }

        val bestTime = parkour.times.values.min()

        MessageBuilder(MessagesConfig.Command.Top.Parkour.header)
            .values(
                mapOf(
                    "name" to parkour.name,
                    "difficulty" to parkour.difficulty.coloredName,
                    "amount" to amount
                )
            )
            .buildAndSend(sender)

        parkour.times.entries
            .groupBy { it.value }
            .toSortedMap()
            .asIterable()
            .take(amount)
            .forEachIndexed { i, (ticks, records) ->
                val holders = records
                    .mapNotNull { Bukkit.getOfflinePlayer(it.key).name }
                    .joinToString(", ")
                val (time, unit) = TickFormatter.format(ticks)

                MessageBuilder(MessagesConfig.Command.Top.Parkour.entry)
                    .values(
                        mapOf(
                            "rank" to i + 1,
                            "player" to holders,
                            "time" to time,
                            "unit" to unit.toMessageValue(),
                            "delta" to formatDelta(ticks, bestTime)
                        )
                    )
                    .buildAndSend(sender)
            }
    }

    private fun handleGlobalEntries(sender: CommandSender, amount: Int) {
        val recordsByPlayer = ParkourManager.recordsByPlayer()

        if (recordsByPlayer.isEmpty()) {
            MessageBuilder(MessagesConfig.Command.Top.Global.empty)
                .error()
                .buildAndSend(sender)
            return
        }

        MessageBuilder(MessagesConfig.Command.Top.Global.header)
            .values(mapOf("amount" to amount))
            .buildAndSend(sender)

        recordsByPlayer.entries
            .groupBy { it.value }
            .toSortedMap()
            .asIterable()
            .reversed()
            .take(amount)
            .forEachIndexed { index, (records, players) ->
                val playersString = players
                    .mapNotNull { Bukkit.getOfflinePlayer(it.key).name }
                    .joinToString(", ")

                MessageBuilder(MessagesConfig.Command.Top.Global.entry)
                    .values(
                        mapOf(
                            "rank" to index + 1,
                            "player" to playersString,
                            "records" to records
                        )
                    )
                    .buildAndSend(sender)
            }
    }

    private fun CommandSender.showUsage() {
        MessageBuilder(
            """
            /top
            /top global (<amount>)
            /top id (<amount>)
            /top help
        """.trimIndent()
        )
            .buildAndSend(this)
    }

    private fun formatDelta(time: Long, bestTime: Long): String {
        return if (time - bestTime == 0L) {
            "${ChatColor.GOLD}✫"
        } else {
            "${ChatColor.RED}-${TickFormatter.format(time - bestTime).first}"
        }
    }
}
