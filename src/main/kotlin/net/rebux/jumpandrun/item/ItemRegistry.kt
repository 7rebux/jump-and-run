package net.rebux.jumpandrun.item

import net.minecraft.server.v1_8_R3.NBTTagCompound
import net.rebux.jumpandrun.Plugin
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
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

    fun onInteract(itemStack: ItemStack, player: Player) {
        val tag = CraftItemStack.asNMSCopy(itemStack)?.tag
            ?: return

        if (tag.hasKey(Plugin.ID_TAG)) {
            items[tag.getInt(Plugin.ID_TAG)]?.onInteract(player)
        }
    }

    fun getItemStack(id: Int): ItemStack {
        return itemStacks.getOrPut(id) {
            val itemStack = items[id]!!.createItemStack()
            val nmsCopy = CraftItemStack.asNMSCopy(itemStack)

            nmsCopy.tag = nmsCopy.tag ?: NBTTagCompound()
            nmsCopy.tag.setInt(Plugin.ID_TAG, id)

            return@getOrPut CraftItemStack.asCraftMirror(nmsCopy)
        }
    }
}
