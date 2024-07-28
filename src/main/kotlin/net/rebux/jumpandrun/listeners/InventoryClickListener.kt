package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.events.ParkourJoinEvent
import net.rebux.jumpandrun.getTag
import net.rebux.jumpandrun.item.impl.MenuItem
import net.rebux.jumpandrun.parkour.ParkourManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import net.rebux.jumpandrun.api.PlayerDataManager.data

object InventoryClickListener : Listener {

  @EventHandler
  fun onClick(event: InventoryClickEvent) {
    val item = event.currentItem

    if (item == null || item.type == Material.AIR || item.amount == 0) {
      return
    }

    val parkourTag = item.getTag(Plugin.PARKOUR_TAG)
    val pageTag = item.getTag(Plugin.PAGE_TAG)

    parkourTag?.let {
      event.isCancelled = true
      handleParkourTag(event.currentItem!!, event)
    }
    pageTag?.let {
      event.isCancelled = true
      handlePageTag(event.currentItem!!, event)
    }
  }

  private fun handleParkourTag(itemStack: ItemStack, event: InventoryClickEvent) {
    val player = event.whoClicked as Player
    val id = itemStack.getTag(Plugin.PARKOUR_TAG)!!
    val parkour = ParkourManager.parkours[id]
      ?: error("Parkour with id=$id could not be found!")

    // Prevent starting a parkour when the player is in practice mode
    if (player.data.inPractice) {
      // TODO: message template
      player.sendMessage("Can't start parkour while in practice mode")
      return
    }

    Bukkit.getPluginManager().callEvent(ParkourJoinEvent(player, parkour))
  }

  private fun handlePageTag(itemStack: ItemStack, event: InventoryClickEvent) {
    val player = event.whoClicked as Player
    val page = itemStack.getTag(Plugin.PAGE_TAG)!!
    val step = itemStack.getTag(Plugin.PAGE_STEP_TAG)!!

    MenuItem.openInventory(player, page + step)
    event.isCancelled = true
  }
}
