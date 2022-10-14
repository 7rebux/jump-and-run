package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.item.Item
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class RestartItem : Item() {

    override fun getItemStack(): ItemStack {
        return Builder()
            .material(Material.REDSTONE)
            .displayName("${ChatColor.RED}Neustart")
            .build()
    }

    override fun onInteract(player: Player) {
        TODO("Restart parkour")
    }
}
