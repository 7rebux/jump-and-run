package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.Main
import net.rebux.jumpandrun.item.Item
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class CheckpointItem : Item() {

    override fun getItemStack(): ItemStack {
        return Builder()
            .material(Material.INK_SACK)
            .durability(1)
            .displayName("${ChatColor.RED}Zurück zum Checkpoint")
            .build()
    }

    override fun onInteract(player: Player) {
        player.teleport(Main.instance.checkpoints[player])
    }
}
