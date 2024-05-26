package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.data
import net.rebux.jumpandrun.item.ItemRegistry
import net.rebux.jumpandrun.item.impl.CheckpointItem
import net.rebux.jumpandrun.msgTemplate
import net.rebux.jumpandrun.utils.InventoryUtil
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
      sender.msgTemplate("commands.playersOnly")
      return true
    }

    return if (sender.data.isInPracticeMode()) {
      disablePracticeMode(sender)
    } else {
      enablePracticeMode(sender)
    }
  }

  fun enablePracticeMode(player: Player): Boolean {
    val practiceData = player.data.practiceData

    practiceData.apply {
      startLocation = player.location
    }

    // Pause parkour timer when player is in a parkour
    if (player.data.isInParkour()) {
      player.data.parkourData.timer.pause()
    }
    // Only save inventory when not in a parkour since the inventory is already
    // saved when the player is in a parkour
    else {
      InventoryUtil.saveInventory(player)
      player.inventory.clear()
      player.inventory.setItem(0, ItemRegistry.getItemStack(CheckpointItem.id))
    }

    // TODO: Print values like in my other parkour plugin
    player.sendMessage("Enabled practice mode")

    return true
  }

  fun disablePracticeMode(player: Player): Boolean {
    val practiceData = player.data.practiceData

    player.teleport(practiceData.startLocation)
    practiceData.apply {
      timer.stop()
      startLocation = null
      endLocation = null
    }

    // Only load the inventory when the player is not in a parkour
    if (!player.data.isInParkour()) {
      InventoryUtil.loadInventory(player)
    }

    // TODO: message template
    player.sendMessage("Disabled practice mode")

    return true
  }
}
