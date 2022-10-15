package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.msgTemplate
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object TopCommand : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        when (sender) {
            !is Player -> sender.msgTemplate("commands.playersOnly")
            !in Instance.plugin.active -> sender.msgTemplate("commands.top.invalid")
            else -> TODO("List top 5/10")
        }

        return true
    }
}
