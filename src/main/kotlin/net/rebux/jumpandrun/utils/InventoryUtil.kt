package net.rebux.jumpandrun.utils

import org.bukkit.entity.Player
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object InventoryUtil {

    private val inventories = hashMapOf<Player, ArrayList<Pair<ItemStack, Int>>>()

    fun saveInventory(player: Player) {
        inventories[player] = arrayListOf()

        for (i in 0..player.inventory.size) {
            // check if item is valid
            if (player.inventory.getItem(i)?.type in listOf(null, Material.AIR))
                continue

            inventories[player]!! += Pair(player.inventory.getItem(i).clone(), i)
        }
    }

    fun loadInventory(player: Player) {
        // clear current inventory
        player.inventory.clear()

        // set items
        inventories[player]?.forEach {
            player.inventory.setItem(it.second, it.first)
        }

        // remove inventory save
        inventories.remove(player)
    }
}
