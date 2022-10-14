package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.item.Item
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.inventory.ItemStack

class LeaveItem : Item() {

    override fun getItemStack(): ItemStack {
        return Builder()
            .material(Material.INK_SACK)
            .durability(8)
            .displayName("${ChatColor.RED}Verlassen ${ChatColor.GRAY}(/spawn)")
            .build()
    }

    override fun onInteract(player: Player) {
        // teleport to spawn
        player.performCommand("spawn")
        Bukkit.getPluginManager().callEvent(PlayerCommandPreprocessEvent(player, "/spawn"))
    }
}
