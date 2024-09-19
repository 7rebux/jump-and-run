package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.events.ParkourJoinEvent
import net.rebux.jumpandrun.getTag
import net.rebux.jumpandrun.inventory.menu.MenuCategory
import net.rebux.jumpandrun.inventory.menu.MenuFilter
import net.rebux.jumpandrun.inventory.menu.MenuInventory
import net.rebux.jumpandrun.inventory.menu.MenuSorting
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
        val filterTag = item.getTag(Plugin.FILTER_TAG)

        idTag?.let { event.isCancelled = true }
        parkourTag?.let { handleParkourTag(event.currentItem!!, event) }
        pageStepTag?.let { handlePageStepTag(event.currentItem!!, event) }
        categoryTag?.let { handleCategoryTag(event) }
        sortingTag?.let { handleSortingTag(event) }
        filterTag?.let { handleFilterTag(event) }
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
        MenuInventory.open(player)
    }

    private fun handleCategoryTag(event: InventoryClickEvent) {
        event.isCancelled = true

        val player = event.whoClicked as Player
        val categoryIndex = player.data.menuState.category.ordinal

        player.data.menuState.apply {
            this.category = MenuCategory.entries.getOrNull(categoryIndex + 1) ?: MenuCategory.entries.first()
            this.page = 0
        }
        MenuInventory.open(player)
    }

    private fun handleSortingTag(event: InventoryClickEvent) {
        event.isCancelled = true

        val player = event.whoClicked as Player
        val sortingIndex = player.data.menuState.sorting.ordinal

        player.data.menuState.sorting =
            MenuSorting.entries.getOrNull(sortingIndex + 1) ?: MenuSorting.entries.first()
        MenuInventory.open(player)
    }

    private fun handleFilterTag(event: InventoryClickEvent) {
        event.isCancelled = true

        val player = event.whoClicked as Player
        val filterIndex = player.data.menuState.filter.ordinal

        player.data.menuState.apply {
            this.filter = MenuFilter.entries.getOrNull(filterIndex + 1) ?: MenuFilter.entries.first()
            this.page = 0
        }
        MenuInventory.open(player)
    }
}
