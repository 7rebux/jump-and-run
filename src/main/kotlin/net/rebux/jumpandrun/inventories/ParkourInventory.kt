package net.rebux.jumpandrun.inventories

import net.rebux.jumpandrun.Main
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class ParkourInventory(private val player: Player?) {
    val inventory = Bukkit.createInventory(null, 36, "Parcours");

    init {
        if (player != null) loadContents()
    }

    private fun loadContents() {
        Main.instance.parkourManager.parkours.forEach { inventory.addItem(it.getItem(player!!)) }
    }
}