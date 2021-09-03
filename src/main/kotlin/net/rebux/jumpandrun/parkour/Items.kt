package net.rebux.jumpandrun.parkour

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object Items {
    fun getLeaveItem(): ItemStack {
        val item = ItemStack(Material.INK_SACK)
        val meta = item.itemMeta

        item.durability = 8

        meta.displayName = "${ChatColor.RED}Jump & Run verlassen ${ChatColor.GRAY}(/spawn)"

        item.itemMeta = meta;

        return item
    }

    fun getCheckpointItem(): ItemStack {
        val item = ItemStack(Material.INK_SACK)
        val meta = item.itemMeta
        val lore = arrayListOf<String>()

        item.durability = 1

        meta.displayName = "${ChatColor.RED}Zurück zum Checkpoint"

        lore.add("${ChatColor.GRAY}Im Inventar anklicken um den Parkour neuzustarten.")

        meta.lore = lore
        item.itemMeta = meta;

        return item
    }
}