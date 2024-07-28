package net.rebux.jumpandrun.utils

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object InventoryCache {

  val inventories = hashMapOf<Player, Map<Int, ItemStack>>()

  fun Player.saveInventory() {
    inventories[this] = buildMap {
      val player = this@saveInventory

      for (i in 0..player.inventory.size) {
        if (player.inventory.getItem(i)?.type in listOf(null, Material.AIR)) {
          continue
        }

        this[i] = player.inventory.getItem(i)!!.clone()
      }
    }
  }

  fun Player.loadInventory() {
    this.inventory.clear()
    inventories.remove(this)?.forEach { (slot, itemStack) ->
      this.inventory.setItem(slot, itemStack)
    }
  }
}
