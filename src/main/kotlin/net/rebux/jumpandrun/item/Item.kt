package net.rebux.jumpandrun.item

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

/**
 * A wrapper class that contains a [ItemStack] and an interact event
 */
abstract class Item {
    abstract fun createItemStack(): ItemStack

    open fun onInteract(player: Player) { }

    data class Builder(
        private var material: Material = Material.AIR,
        private var displayName: String? = null,
        private var lore: List<String>? = null,
        private var durability: Short? = null
    ) {

        fun material(material: Material) = apply { this.material = material }

        fun displayName(displayName: String) = apply { this.displayName = displayName }

        fun lore(lore: List<String>) = apply { this.lore = lore }

        fun durability(durability: Short) = apply { this.durability = durability }

        fun build(): ItemStack {
            val itemStack = ItemStack(material)
            val itemMeta = itemStack.itemMeta

            durability?.let { itemStack.durability = it }
            displayName?.let { itemMeta.displayName = it }
            lore?.let { itemMeta.lore = it }

            itemStack.itemMeta = itemMeta

            return itemStack
        }
    }

    data class SkullBuilder(
        private var displayName: String? = null,
        private var username: String? = null
    ) {

        fun displayName(displayName: String) = apply { this.displayName = displayName }

        fun username(username: String) = apply { this.username = username }

        fun build(): ItemStack {
            val itemStack = ItemStack(Material.SKULL_ITEM, 1, 3)
            val itemMeta = itemStack.itemMeta as SkullMeta

            displayName?.let { itemMeta.displayName = it }
            username?.let { itemMeta.owner = it }

            itemStack.itemMeta = itemMeta

            return itemStack
        }
    }
}
