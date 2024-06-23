package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.getTag
import net.rebux.jumpandrun.item.impl.MenuItem
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class InventoryClickListener(private val plugin: Plugin) : Listener {

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val parkourTag = event.currentItem?.getTag(Plugin.PARKOUR_TAG)
        val pageTag = event.currentItem?.getTag(Plugin.PAGE_TAG)

        parkourTag?.let { handleParkourTag(event.currentItem!!, event) }
        pageTag?.let { handlePageTag(event.currentItem!!, event) }
    }

    private fun handleParkourTag(itemStack: ItemStack, event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val id = itemStack.getTag(Plugin.PARKOUR_TAG)!!
        val parkour = plugin.parkourManager.parkours[id]

        parkour?.start(player) ?: error("Parkour #$id not found!")
        event.isCancelled = true
    }

    private fun handlePageTag(itemStack: ItemStack, event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val page = itemStack.getTag(Plugin.PAGE_TAG)!!
        val step = itemStack.getTag(Plugin.PAGE_STEP_TAG)!!

        MenuItem.openInventory(player, page + step)
        event.isCancelled = true
    }
}
