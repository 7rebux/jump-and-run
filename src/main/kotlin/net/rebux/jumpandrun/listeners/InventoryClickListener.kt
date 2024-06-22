package net.rebux.jumpandrun.listeners

import de.tr7zw.changeme.nbtapi.NBT
import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.item.impl.MenuItem
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class InventoryClickListener(private val plugin: Plugin) : Listener {

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.currentItem == null) {
            return
        }

        val itemStack = event.currentItem!!
        val parkourIdTag: Int? = NBT.get<Int>(event.currentItem) { nbt -> nbt.getInteger(Plugin.PARKOUR_TAG) }
        val pageTag: Int? = NBT.get<Int>(event.currentItem) { nbt -> nbt.getInteger(Plugin.PAGE_TAG) }

        if (parkourIdTag != null) {
            handleParkourTag(itemStack, event)
        } else if (pageTag != null) {
            handlePageTag(itemStack, event)
        }
    }

    private fun handleParkourTag(itemStack: ItemStack, event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val id = NBT.get<Int>(itemStack) { nbt -> nbt.getInteger(Plugin.PARKOUR_TAG) }
        val parkour = plugin.parkourManager.parkours[id]

        parkour?.start(player) ?: error("Parkour #$id not found!")
        event.isCancelled = true
    }

    private fun handlePageTag(itemStack: ItemStack, event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val page = NBT.get<Int>(itemStack) { nbt -> nbt.getInteger(Plugin.PAGE_TAG) }
        val step = NBT.get<Int>(itemStack) { nbt -> nbt.getInteger(Plugin.PAGE_STEP_TAG) }

        MenuItem.openInventory(player, page + step)
        event.isCancelled = true
    }
}
