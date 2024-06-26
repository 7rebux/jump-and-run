package net.rebux.jumpandrun.item

import de.tr7zw.changeme.nbtapi.NBT
import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.getTag
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

  fun handleInteraction(itemStack: ItemStack, player: Player) {
    itemStack.getTag(Plugin.ID_TAG)?.let {
      items[it]?.onInteract(player)
        ?: error("Found ItemStack with custom id which is not registered!")
    }
  }

  fun getItemStack(id: Int): ItemStack {
    return itemStacks.getOrPut(id) {
      items[id]?.createItemStack().also {
        NBT.modify(it) { nbt ->
          nbt.setInteger(Plugin.ID_TAG, id)
        }
      } ?: error("Could not find ItemStack with id $id in registry!")
    }
  }
}
