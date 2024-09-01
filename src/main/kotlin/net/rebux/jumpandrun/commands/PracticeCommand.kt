package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.events.PracticeDisableEvent
import net.rebux.jumpandrun.events.PracticeEnableEvent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PracticeCommand : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be called as a player!")
            return true
        }

        if (sender.data.inPractice) {
            Bukkit.getPluginManager().callEvent(PracticeDisableEvent(sender))
        } else {
            Bukkit.getPluginManager().callEvent(PracticeEnableEvent(sender))
        }
        return true
    }
}
