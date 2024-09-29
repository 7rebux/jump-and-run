package net.rebux.jumpandrun.item

import de.tr7zw.nbtapi.NBT
import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.getTag
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/** A registry that stores custom [Item] instances and caches its [ItemStack]s */
object ItemRegistry {

    private val items = ConcurrentHashMap<Int, Item>()
    private val itemStacks = ConcurrentHashMap<Int, ItemStack>()
    private val nextId = AtomicInteger(1)

    fun register(item: Item): Int {
        return nextId.getAndIncrement().also { id -> items[id] = item }
    }

    fun handleInteraction(itemStack: ItemStack, event: PlayerInteractEvent) {
        val id = itemStack.getTag(Plugin.ID_TAG) ?: return
        val item = items[id]
            ?: error("Found ItemStack with custom id which is not registered!")

        if (event.action in listOf(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK)) {
            item.onInteract(event.player)
        } else if (event.action == Action.LEFT_CLICK_BLOCK) {
            item.onLeftClickBlock(event.player, event.clickedBlock!!)
        }
    }

    fun getItemStack(id: Int): ItemStack {
        return itemStacks.getOrPut(id) {
            items[id]?.createItemStack().also {
                NBT.modify(it) { nbt -> nbt.setInteger(Plugin.ID_TAG, id) }
            } ?: error("Could not find ItemStack with id $id in registry!")
        }
    }
}
