package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Main
import net.rebux.jumpandrun.parkour.Items
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class InventoryListener: Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.currentItem?.type == Material.AIR)
            return

        if (event.currentItem.itemMeta.displayName == Items.getCheckpointItem().itemMeta.displayName)
            return

        if (event.inventory.name == "Parcours") {
            for (parkour in Main.instance.parkourManager.parkours) {
                if (event.currentItem.itemMeta.displayName == parkour.getItem(event.whoClicked as Player).itemMeta.displayName && event.currentItem.type == parkour.material)
                    parkour.start(event.whoClicked as Player)
            }
        }
    }
}