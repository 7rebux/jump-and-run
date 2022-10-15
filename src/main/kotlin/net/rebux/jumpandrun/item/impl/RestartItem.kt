package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.item.Item
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class RestartItem : Item() {

    private val plugin = Instance.plugin

    override fun getItemStack(): ItemStack {
        return Builder()
            .material(Material.REDSTONE)
            .displayName(plugin.config.getString("items.restart"))
            .build()
    }

    override fun onInteract(player: Player) {
        val location = plugin.active[player]!!.location

        plugin.checkpoints[player] = location
        plugin.times.remove(player)

        player.teleport(location)
    }
}
