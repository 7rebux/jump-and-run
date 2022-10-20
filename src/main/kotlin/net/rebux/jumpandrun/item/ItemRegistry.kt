package net.rebux.jumpandrun.item

import net.minecraft.server.v1_8_R3.NBTTagCompound
import net.rebux.jumpandrun.Plugin
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * A registry that stores custom [Item] instances and caches it's [ItemStack]s
 */
object ItemRegistry {

    private val items = ConcurrentHashMap<Int, Item>()
    private val itemStacks = ConcurrentHashMap<Int, ItemStack>()
    private val nextId = AtomicInteger(1)

    /**
     * Registers the given [item]
     *
     * @param item the [Item] to register
     * @return the id registered to
     */
    fun register(item: Item): Int {
        val id = nextId.getAndIncrement()
        items[id] = item
        return id
    }

    /**
     * Calls the interact event on the given [itemStack] if the item is registered
     *
     * @param itemStack the [ItemStack] interacted with
     * @param player the [Player] who interacted
     */
    fun onInteract(itemStack: ItemStack, player: Player) {
        val nmsCopy: net.minecraft.server.v1_8_R3.ItemStack? = CraftItemStack.asNMSCopy(itemStack)

        if (nmsCopy?.tag?.hasKey(Plugin.ID_TAG) != true)
            return

        items[nmsCopy.tag.getInt(Plugin.ID_TAG)]?.onInteract(player)
            ?: error("Found item with id tag, but it is not registered!")
    }

    /**
     * Caches the [ItemStack] associated to the given [id], if not already done
     *
     * @return the [ItemStack] associated to the given [id]
     */
    fun getItemStack(id: Int): ItemStack {
        return itemStacks.getOrPut(id) {
            val itemStack = items[id]!!.createItemStack()
            val nmsCopy = CraftItemStack.asNMSCopy(itemStack)

            nmsCopy.tag = nmsCopy.tag ?: NBTTagCompound()
            nmsCopy.tag.setInt(Plugin.ID_TAG, id)

            CraftItemStack.asCraftMirror(nmsCopy)
        }
    }
}
