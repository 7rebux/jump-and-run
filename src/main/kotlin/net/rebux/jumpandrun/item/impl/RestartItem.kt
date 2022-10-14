package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.Main
import net.rebux.jumpandrun.item.Item
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class RestartItem : Item() {

    private val plugin = Main.instance

    override fun getItemStack(): ItemStack {
        return Builder()
            .material(Material.REDSTONE)
            .displayName("${ChatColor.RED}Neustart")
            .build()
    }

    override fun onInteract(player: Player) {
        val location = plugin.active[player]!!.location

        plugin.checkpoints[player] = location
        plugin.times.remove(player)

        player.teleport(location)
    }
}
