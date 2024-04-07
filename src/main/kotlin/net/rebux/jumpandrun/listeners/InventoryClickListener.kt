package net.rebux.jumpandrun.listeners

import net.minecraft.server.v1_8_R3.ItemStack
import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.item.impl.MenuItem
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class InventoryClickListener(private val plugin: Plugin) : Listener {

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.currentItem == null) {
            return
        }

        val nmsCopy = CraftItemStack.asNMSCopy(event.currentItem)

        if (nmsCopy.hasTag()) {
            when {
                nmsCopy.tag.hasKey(Plugin.PARKOUR_TAG) -> handleParkourTag(nmsCopy, event)
                nmsCopy.tag.hasKey(Plugin.PAGE_TAG) -> handlePageTag(nmsCopy, event)
            }
        }
    }

    private fun handleParkourTag(nmsCopy: ItemStack, event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val id = nmsCopy.tag.getInt(Plugin.PARKOUR_TAG)
        val parkour = plugin.parkourManager.parkours[id]

        parkour?.start(player) ?: error("Parkour #$id not found!")
        event.isCancelled = true
    }

    private fun handlePageTag(nmsCopy: ItemStack, event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val page = nmsCopy.tag.getInt(Plugin.PAGE_TAG)
        val step = nmsCopy.tag.getInt(Plugin.PAGE_STEP_TAG)

        MenuItem.openInventory(player, page + step)
        event.isCancelled = true
    }
}