package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.item.Item
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class CheckpointItem : Item() {

    override fun getItemStack(): ItemStack {
        return Builder()
            .material(Material.INK_SACK)
            .durability(1)
            .displayName(Instance.plugin.config.getString("items.checkpoint"))
            .build()
    }

    override fun onInteract(player: Player) {
        player.teleport(Instance.plugin.checkpoints[player])
    }
}
