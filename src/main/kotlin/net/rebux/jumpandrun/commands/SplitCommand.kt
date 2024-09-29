package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.api.ParkourSplit
import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.utils.MessageBuilder
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class SplitCommand(private val plugin: Plugin) : CommandExecutor, TabCompleter {

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

        if (!sender.data.inParkour) {
            sender.sendMessage("This command can only be called while in parkour mode!")
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
    ): MutableList<String> {
        return emptyList<String>().toMutableList()
    }

    private fun handleListCommand(player: Player) {
        player.data.parkourData.splits.forEachIndexed { index, split ->
            player.sendMessage("#$index ${split.block.type} (${split.block.location})")
        }
    }

    private fun handleAddCommand(player: Player) {
        val targetBlock = player.getTargetBlockExact(4)

        if (targetBlock == null) {
            player.sendMessage("")
            return
        }

        player.data.parkourData.splits += ParkourSplit(targetBlock)
        player.sendMessage("Successfully added ${targetBlock.type} (${targetBlock.location})")
    }

    private fun handleRemoveCommand(player: Player, index: Int?) {
        if (index == null || index !in player.data.parkourData.splits.indices) {
            player.sendMessage("Please provide a valid index")
        }

        player.data.parkourData.splits.removeAt(index!!)
        player.sendMessage("Successfully removed")
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
