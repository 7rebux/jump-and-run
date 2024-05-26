package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.data
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
            .material(Material.INK_SACK)
            .durability(8)
            .displayName(Instance.plugin.config.getString("items.leave"))
            .build()
    }

    override fun onInteract(player: Player) {
        // Prevent leaving parkour while in practice mode
        if (player.data.isInPracticeMode()) {
            // TODO: message template
            player.sendMessage("Can't leave parkour while in practice mode")
            return
        }

        player.performCommand("spawn")
        Bukkit.getPluginManager().callEvent(PlayerCommandPreprocessEvent(player, "/spawn"))
    }
}
