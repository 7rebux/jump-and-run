package net.rebux.jumpandrun.item

import de.tr7zw.changeme.nbtapi.NBT
import net.rebux.jumpandrun.Plugin
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * A registry that stores custom [Item] instances and caches its [ItemStack]s
 */
object ItemRegistry {

    private val items = ConcurrentHashMap<Int, Item>()
    private val itemStacks = ConcurrentHashMap<Int, ItemStack>()
    private val nextId = AtomicInteger(1)

    fun register(item: Item): Int {
        return nextId.getAndIncrement().also { id ->
            items[id] = item
        }
    }

    // TODO: Use let expression
    fun onInteract(itemStack: ItemStack, player: Player) {
        val id: Int? = NBT.get<Int>(itemStack) { nbt -> nbt.getInteger(Plugin.ID_TAG) }

        if (id != null) {
            items[id]?.onInteract(player)
        }
    }

    fun getItemStack(id: Int): ItemStack {
        return itemStacks.getOrPut(id) {
            val itemStack = items[id]!!.createItemStack()

            NBT.modify(itemStack) { nbt ->
                nbt.setInteger(Plugin.ID_TAG, id)
            }

            return@getOrPut itemStack
        }
    }
}
