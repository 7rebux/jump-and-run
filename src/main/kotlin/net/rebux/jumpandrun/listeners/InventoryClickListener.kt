package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.events.ParkourJoinEvent
import net.rebux.jumpandrun.getEnumTag
import net.rebux.jumpandrun.getTag
import net.rebux.jumpandrun.item.impl.MenuItem
import net.rebux.jumpandrun.parkour.ParkourDifficulty
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

        // TODO: This cancels events for almost everything
        if (item == null || item.type == Material.AIR || item.amount == 0) {
            event.isCancelled = true
            return
        }

        val idTag = item.getTag(Plugin.ID_TAG)
        val parkourTag = item.getTag(Plugin.PARKOUR_TAG)
        val pageTag = item.getTag(Plugin.PAGE_TAG)
        val difficultyTag = item.getTag(Plugin.DIFFICULTY_TAG)

        idTag?.let { event.isCancelled = true }
        parkourTag?.let { handleParkourTag(event.currentItem!!, event) }
        pageTag?.let { handlePageTag(event.currentItem!!, event) }
        difficultyTag?.let { handleDifficultyTag(event.currentItem!!, event) }
    }

    private fun handleParkourTag(itemStack: ItemStack, event: InventoryClickEvent) {
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

    private fun handlePageTag(itemStack: ItemStack, event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val page = itemStack.getTag(Plugin.PAGE_TAG)!!
        val step = itemStack.getTag(Plugin.PAGE_STEP_TAG)!!

        MenuItem.openInventory(player, page + step)
        event.isCancelled = true
    }

    private fun handleDifficultyTag(itemStack: ItemStack, event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val difficulty = itemStack.getEnumTag(Plugin.DIFFICULTY_TAG, ParkourDifficulty::class.java)

        MenuItem.selectedDifficulty[player] = difficulty
        MenuItem.openInventory(player, 0)
        event.isCancelled = true
    }
}
