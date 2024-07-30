package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.item.ItemRegistry
import org.bukkit.Material
import org.bukkit.entity.Minecart
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

object PlayerInteractListener : Listener {

  @EventHandler
  fun onInteract(event: PlayerInteractEvent) {
    val itemStack = getItemInMainHand(event.player)

    if (event.hand == EquipmentSlot.OFF_HAND) {
      return
    }

    if (event.action !in listOf(Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR)) {
      return
    }

    if (itemStack == null || itemStack.type == Material.AIR || itemStack.amount == 0) {
      return
    }

    ItemRegistry.handleInteraction(itemStack, event.player)
  }

  // The lobby plugin of "auragames.de" prevents interacting with mine carts
  // So we allow the interaction again when the player is in a parkour
  @EventHandler(priority = EventPriority.HIGH)
  fun onEntityInteract(event: PlayerInteractEntityEvent) {
    if (event.player.data.inParkour && event.rightClicked is Minecart) {
      event.rightClicked.setPassenger(event.player)
    }
  }

  private fun getItemInMainHand(player: Player): ItemStack? {
    try {
      val item = player.inventory.itemInMainHand

      if (item.type == Material.AIR) {
        return null
      }

      return item
    } catch (_: NoSuchMethodError) {
      // Fallback for 1.8 servers
      return player.itemInHand
    }
  }
}
