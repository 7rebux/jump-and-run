package net.rebux.jumpandrun.commands

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.MessagesConfig
import net.rebux.jumpandrun.item.ItemRegistry
import net.rebux.jumpandrun.item.impl.PracticeItem
import net.rebux.jumpandrun.safeTeleport
import net.rebux.jumpandrun.utils.InventoryCache.loadInventory
import net.rebux.jumpandrun.utils.InventoryCache.saveInventory
import net.rebux.jumpandrun.utils.MessageBuilder
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.NumberConversions

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
    // TODO: Create some kind of persistent state for player inventory and gamemode management
    else {
      player.saveInventory()
      player.inventory.clear()
      player.inventory.setItem(0, ItemRegistry.getItemStack(PracticeItem.id))
    }

    MessageBuilder(MessagesConfig.Command.Practice.enabled)
      .values(
        mapOf(
          "x" to "%.3f".format(player.location.x),
          "y" to "%.3f".format(player.location.y),
          "z" to "%.3f".format(player.location.z),
          "direction" to player.location.facing(),
          "yaw" to "%.3f".format(player.location.yaw),
          "pitch" to "%.3f".format(player.location.pitch)))
      .buildAndSend(player)

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

    MessageBuilder(MessagesConfig.Command.Practice.disabled)
      .buildAndSend(player)

    return true
  }

  private fun Location.facing(): String {
    return when (NumberConversions.floor((this.yaw * 4.0F / 360.0F).toDouble() + 0.5) and 3) {
      0 -> "Z+"
      1 -> "X-"
      2 -> "Z-"
      else -> "X+"
    }
  }
}
