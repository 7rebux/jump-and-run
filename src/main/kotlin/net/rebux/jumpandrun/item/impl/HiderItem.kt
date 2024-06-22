package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.data
import net.rebux.jumpandrun.item.Item
import net.rebux.jumpandrun.item.ItemRegistry
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object HiderItem : Item() {

  val id = ItemRegistry.register(this)

  override fun createItemStack(): ItemStack {
    return Builder()
      .material(Material.INK_SACK)
      .durability(1) // TODO kp
      .displayName(Instance.plugin.config.getString("items.hider"))
      .build()
  }

  override fun onInteract(player: Player) {
    if (player.data.playersHidden) {
      Bukkit.getOnlinePlayers().forEach(player::showPlayer)
      player.data.playersHidden = false
    } else {
      Bukkit.getOnlinePlayers().forEach(player::hidePlayer)
      player.data.playersHidden = true
    }
  }
}
