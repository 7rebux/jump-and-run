package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.item.ItemRegistry
import org.bukkit.entity.Minecart
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class PlayerInteractListener(private val plugin: Plugin) : Listener {

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item: ItemStack? = player.itemInHand

        if (event.action !in listOf(Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR)) {
            return
        }

        item?.let { ItemRegistry.onInteract(it, player) }
    }

    // This is only needed to fix a bug on "auragames.de"
    // TODO: Is this still needed?
    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityInteract(event: PlayerInteractEntityEvent) {
        if (event.player.world.name == plugin.config.getString("worldName")
            && event.rightClicked is Minecart) {

            event.rightClicked.setPassenger(event.player)
        }
    }
}
