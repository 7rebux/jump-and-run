package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.api.MenuCategory
import net.rebux.jumpandrun.api.MenuSorting
import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.events.ParkourJoinEvent
import net.rebux.jumpandrun.getEnumTag
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

object InventoryClickListener : Listener {

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val item = event.currentItem

        // TODO: IMPORTANT!!! This cancels events for almost everything
        if (item == null || item.type == Material.AIR || item.amount == 0) {
            event.isCancelled = true
            return
        }

        val idTag = item.getTag(Plugin.ID_TAG)
        val parkourTag = item.getTag(Plugin.PARKOUR_TAG)
        val pageStepTag = item.getTag(Plugin.PAGE_STEP_TAG)
        val categoryTag = item.getTag(Plugin.CATEGORY_TAG)
        val sortingTag = item.getTag(Plugin.SORTING_TAG)

        idTag?.let { event.isCancelled = true }
        parkourTag?.let { handleParkourTag(event.currentItem!!, event) }
        pageStepTag?.let { handlePageStepTag(event.currentItem!!, event) }
        categoryTag?.let { handleCategoryTag(event.currentItem!!, event) }
        sortingTag?.let { handleSortingTag(event) }
    }

    private fun handleParkourTag(itemStack: ItemStack, event: InventoryClickEvent) {
        event.isCancelled = true

        val player = event.whoClicked as Player
        val id = itemStack.getTag(Plugin.PARKOUR_TAG)!!
        val parkour =
            ParkourManager.parkours[id] ?: error("Parkour with id=$id could not be found!")

        // Prevent starting a parkour when the player is in practice mode
        if (player.data.inPractice) {
            return
        }

        Bukkit.getPluginManager().callEvent(ParkourJoinEvent(player, parkour))
    }

    private fun handlePageStepTag(itemStack: ItemStack, event: InventoryClickEvent) {
        event.isCancelled = true

        val player = event.whoClicked as Player
        val step = itemStack.getTag(Plugin.PAGE_STEP_TAG)!!

        player.data.menuState.page += step
        MenuItem.openInventory(player)
    }

    private fun handleCategoryTag(itemStack: ItemStack, event: InventoryClickEvent) {
        event.isCancelled = true

        val player = event.whoClicked as Player
        val category = itemStack.getEnumTag(Plugin.CATEGORY_TAG, MenuCategory::class.java)
            ?: error("Invalid category on item stack")

        player.data.menuState.apply {
            this.category = category
            this.page = 0
        }
        MenuItem.openInventory(player)
    }

    private fun handleSortingTag(event: InventoryClickEvent) {
        event.isCancelled = true

        val player = event.whoClicked as Player
        val sortingIndex = player.data.menuState.sorting.ordinal

        player.data.menuState.sorting =
            MenuSorting.entries.getOrNull(sortingIndex + 1) ?: MenuSorting.entries.first()
        MenuItem.openInventory(player)
    }
}
