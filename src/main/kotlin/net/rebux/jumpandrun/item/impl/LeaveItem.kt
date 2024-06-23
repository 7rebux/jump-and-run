package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.item.Item
import net.rebux.jumpandrun.item.ItemRegistry
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.inventory.ItemStack

object LeaveItem : Item() {

    val id = ItemRegistry.register(this)

    override fun createItemStack(): ItemStack {
        return Builder()
            .material(Material.GRAY_DYE)
            .displayName(Instance.plugin.config.getString("items.leave")!!)
            .build()
    }

    override fun onInteract(player: Player) {
        player.performCommand("spawn")
        Bukkit.getPluginManager().callEvent(PlayerCommandPreprocessEvent(player, "/spawn"))
    }
}
