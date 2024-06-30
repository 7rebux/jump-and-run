package net.rebux.jumpandrun.utils

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object InventoryCache {

  val inventories = hashMapOf<Player, Map<Int, ItemStack>>()
}
