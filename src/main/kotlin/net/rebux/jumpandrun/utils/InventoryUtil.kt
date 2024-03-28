package net.rebux.jumpandrun.utils

import org.bukkit.entity.Player
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object InventoryUtil {
    private val inventories = hashMapOf<Player, ArrayList<Pair<ItemStack, Int>>>()

    fun saveInventory(player: Player) {
        inventories[player] = arrayListOf()

        for (i in 0..player.inventory.size) {
            if (player.inventory.getItem(i)?.type in listOf(null, Material.AIR))
                continue

            inventories[player]!! += Pair(player.inventory.getItem(i).clone(), i)
        }
    }

    fun loadInventory(player: Player) {
        player.inventory.clear()
        inventories[player]?.forEach { player.inventory.setItem(it.second, it.first) }
        inventories.remove(player)
    }
}
