package net.rebux.jumpandrun.inventories

import net.rebux.jumpandrun.Main
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

class ParkourInventory(private val player: Player?) {
    val inventory: Inventory = Bukkit.createInventory(null, 36, "Parcours");

    init {
        if (player != null) loadContents()
    }

    private fun loadContents() {
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance) {
            Main.instance.parkourManager.parkours.forEach { inventory.addItem(it.getItem(player!!)) }
        }
    }
}