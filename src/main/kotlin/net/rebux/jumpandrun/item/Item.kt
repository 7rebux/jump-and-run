package net.rebux.jumpandrun.item

import net.rebux.jumpandrun.config.ItemsConfig
import net.rebux.jumpandrun.utils.itemStack
import net.rebux.jumpandrun.utils.meta
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/** A wrapper class that contains a [ItemStack] and an interact event */
abstract class Item(configName: String) {

    val id = ItemRegistry.register(this)
    val name = ItemsConfig.getName(configName)
    private val enabled = ItemsConfig.isEnabled(configName)
    private val material = ItemsConfig.getMaterial(configName)
    private val slot = ItemsConfig.getSlot(configName)

    abstract fun onInteract(player: Player)

    open fun onLeftClickBlock(player: Player, block: Block) {}

    fun createItemStack(): ItemStack {
        return itemStack(material) {
            meta {
                setDisplayName(name)
            }
        }
    }

    fun addToInventory(player: Player) {
        if (!enabled) {
            return
        }

        player.inventory.setItem(slot, ItemRegistry.getItemStack(id))
    }
}
