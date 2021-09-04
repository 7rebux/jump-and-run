package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Main
import net.rebux.jumpandrun.parkour.Items
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class InventoryListener: Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val clickedItem: ItemStack = event.currentItem

        if (clickedItem.type == Material.AIR)
            return

        if (clickedItem.itemMeta.displayName == Items.getCheckpointItem().itemMeta.displayName)
            // TODO restart

        if (event.inventory.name == "Parcours") {
            for (parkour in Main.instance.parkourManager.parkours) {
                if (clickedItem.itemMeta.displayName == parkour.getItem(event.whoClicked as Player).itemMeta.displayName)
                    parkour.start(event.whoClicked as Player)
            }
        }
    }
}