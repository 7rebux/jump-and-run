package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.item.ItemRegistry
import net.rebux.jumpandrun.item.impl.ResetItem
import net.rebux.jumpandrun.safeTeleport
import net.rebux.jumpandrun.utils.InventoryCache.loadInventory
import net.rebux.jumpandrun.utils.InventoryCache.saveInventory
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

    return if (sender.data.inPractice) {
      disablePracticeMode(sender)
    } else {
      enablePracticeMode(sender)
    }
  }

  private fun enablePracticeMode(player: Player): Boolean {
    val practiceData = player.data.practiceData

    practiceData.apply {
      startLocation = player.location
    }

    // Pause parkour timer when player is in a parkour
    if (player.data.inParkour) {
      player.data.parkourData.timer.pause()
    }
    // Only save inventory when not in a parkour since the inventory is already
    // saved when the player is in a parkour
    else {
      player.saveInventory()
      player.inventory.clear()
      player.inventory.setItem(0, ItemRegistry.getItemStack(ResetItem.id))
    }

    // TODO: Print values like in my other parkour plugin
    player.sendMessage("Enabled practice mode")

    return true
  }

  private fun disablePracticeMode(player: Player): Boolean {
    val practiceData = player.data.practiceData

    player.safeTeleport(practiceData.startLocation!!)
    practiceData.apply {
      timer.stop()
      startLocation = null
    }

    // Only load the inventory when the player is not in a parkour
    if (!player.data.inParkour) {
      player.loadInventory()
    }

    // TODO: message template
    player.sendMessage("Disabled practice mode")

    return true
  }
}
