package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.item.ItemRegistry
import net.rebux.jumpandrun.item.impl.MenuItem
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Minecart
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

/**
 * Contains event listeners for [PlayerInteractEvent], [PlayerInteractEntityEvent] and [InventoryClickEvent]
 */
object InteractionListener: Listener {

    private val plugin = Instance.plugin

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item: ItemStack? = player.itemInHand

        // check if action is correct
        if (event.action !in listOf(Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR))
            return

        // call interact event if item is not null
        item?.let { ItemRegistry.onInteract(item, player) }
    }

    /**
     * "Bug" fix for lobby plugin
     */
    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityInteract(event: PlayerInteractEntityEvent) {
        val player = event.player
        val entity = event.rightClicked

        // check if world is valid
        if (player.world.name != plugin.config.getString("worldName"))
            return

        if (entity is Minecart)
            entity.setPassenger(player)
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val nmsCopy: net.minecraft.server.v1_8_R3.ItemStack? = CraftItemStack.asNMSCopy(event.currentItem)
        val player = event.whoClicked as? Player ?: return

        // check if item is valid
        if (nmsCopy?.hasTag() != true)
            return

        when {
            // parkour items
            nmsCopy.tag.hasKey(Plugin.PARKOUR_TAG) -> {
                val id = nmsCopy.tag.getInt(Plugin.PARKOUR_TAG)
                val parkour = plugin.parkourManager.parkours[id]

                parkour?.start(player) ?: error("Parkour #$id not found!")
                event.isCancelled = true
            }

            // page items
            nmsCopy.tag.hasKey(Plugin.PAGE_TAG) -> {
                val page = nmsCopy.tag.getInt(Plugin.PAGE_TAG)
                val step = nmsCopy.tag.getInt(Plugin.PAGE_STEP_TAG)

                MenuItem.openInventory(player, page + step)
                event.isCancelled = true
            }
        }
    }
}
