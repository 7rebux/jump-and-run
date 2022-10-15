package net.rebux.jumpandrun.item

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class Item {

    abstract fun getItemStack(): ItemStack

    abstract fun onInteract(player: Player)

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
}
