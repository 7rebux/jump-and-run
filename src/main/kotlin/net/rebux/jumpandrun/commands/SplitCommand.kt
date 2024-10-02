package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.api.ParkourSplit
import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.MessagesConfig
import net.rebux.jumpandrun.utils.MessageBuilder
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

object SplitCommand : CommandExecutor, TabCompleter {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            MessageBuilder(MessagesConfig.Command.playersOnly)
                .error()
                .buildAndSend(sender)
            return true
        }

        if (!sender.data.inParkour) {
            MessageBuilder("This command can only be called while in parkour mode!")
                .error()
                .buildAndSend(sender)
            return true
        }

        when (args.firstOrNull()?.lowercase()) {
            "list" -> handleListCommand(sender)
            "add" -> handleAddCommand(sender)
            "remove" -> handleRemoveCommand(sender, args.getOrNull(1)?.toIntOrNull())
            else -> sendUsage(sender)
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        if (args.size == 1) {
            return listOf("list", "add", "remove")
        }

        return emptyList()
    }

    private fun handleListCommand(player: Player) {
        if (player.data.parkourData.splits.isEmpty()) {
            MessageBuilder(MessagesConfig.Command.Split.empty)
                .error()
                .buildAndSend(player)
        } else {
            player.data.parkourData.splits.forEachIndexed { index, split ->
                MessageBuilder(MessagesConfig.Command.Split.entry)
                    .values(
                        mapOf(
                            "index" to index,
                            "block" to split.block.type.name,
                            "x" to split.block.x,
                            "y" to split.block.y,
                            "z" to split.block.z
                        )
                    )
                    .buildAndSend(player)
            }
        }
    }

    private fun handleAddCommand(player: Player) {
        val targetBlock = player.getTargetBlockExact(4)

        if (targetBlock == null) {
            MessageBuilder(MessagesConfig.Command.Split.invalid)
                .error()
                .buildAndSend(player)
            return
        }

        player.data.parkourData.splits += ParkourSplit(targetBlock)
        MessageBuilder(MessagesConfig.Command.Split.added)
            .values(
                mapOf(
                    "block" to targetBlock.type.name,
                    "x" to targetBlock.x,
                    "y" to targetBlock.y,
                    "z" to targetBlock.z
                )
            )
            .buildAndSend(player)
    }

    private fun handleRemoveCommand(player: Player, index: Int?) {
        if (index == null || index !in player.data.parkourData.splits.indices) {
            MessageBuilder("Please provide a valid index!")
                .error()
                .buildAndSend(player)
            return
        }

        player.data.parkourData.splits.removeAt(index)
        MessageBuilder(MessagesConfig.Command.Split.removed)
            .values(mapOf("index" to index))
            .buildAndSend(player)
    }

    private fun sendUsage(sender: CommandSender) {
        MessageBuilder("""
                /split list
                /split add
                /split remove <id>
            """.trimIndent()
        ).buildAndSend(sender)
    }
}
