package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.inventory.menu.MenuInventory
import net.rebux.jumpandrun.item.Item
import org.bukkit.entity.Player

object MenuItem : Item("menu") {

    override fun onInteract(player: Player) {
        MenuInventory.open(player)
    }
}
