package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.item.Item
import net.rebux.jumpandrun.item.ItemRegistry
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * An [Item] implementation that restarts the current parkour
 */
object RestartItem : Item() {

    val id = ItemRegistry.register(this)

    private val plugin = Instance.plugin

    override fun createItemStack(): ItemStack {
        return Builder()
            .material(Material.REDSTONE)
            .displayName(plugin.config.getString("items.restart"))
            .build()
    }

    override fun onInteract(player: Player) {
        val location = plugin.active[player]!!.location

        plugin.checkpoints[player] = location
        plugin.tickCounters.remove(player)

        player.teleport(location)
    }
}
