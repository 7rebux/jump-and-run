package net.rebux.jumpandrun.utils

import org.bukkit.entity.Player
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object InventoryCache {

  private val inventories = hashMapOf<Player, Map<Int, ItemStack>>()

  fun saveInventory(player: Player) {
    inventories[player] = buildMap {
      for (i in 0..player.inventory.size) {
        if (player.inventory.getItem(i)?.type in listOf(null, Material.AIR)) {
          continue
        }

        this[i] = player.inventory.getItem(i)!!.clone()
      }
    }
  }

  fun loadInventory(player: Player) {
    player.inventory.clear()
    inventories.remove(player)?.forEach { (slot, itemStack) ->
      player.inventory.setItem(slot, itemStack)
    }
  }
}
