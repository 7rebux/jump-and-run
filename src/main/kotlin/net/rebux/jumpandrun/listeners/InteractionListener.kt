package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.item.impl.*
import org.bukkit.ChatColor
import org.bukkit.entity.Minecart
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

object InteractionListener: Listener {

    private val plugin = Instance.plugin
    private val items = arrayListOf(
        MenuItem(),
        LeaveItem(),
        RestartItem(),
        CheckpointItem()
    )

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item = player.itemInHand

        // check if action is correct
        if (event.action !in listOf(Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR))
            return

        // check item is valid
        if (item?.itemMeta?.displayName == null)
            return

        // call interact event
        items.forEach {
            if (it.getItemStack().itemMeta.displayName.equals(item.itemMeta.displayName, true))
                it.onInteract(player)
        }
    }

    // "bug" fix for lobby plugin
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
        val item = event.currentItem
        val player = event.whoClicked as? Player // safe cast (null if whoClicked is not a player)

        // check if item is valid
        if (item?.itemMeta?.displayName == null)
            return

        // start parkour
        Instance.plugin.parkourManager.parkours.forEach {
            if (it.name.equals(ChatColor.stripColor(item.itemMeta.displayName), true))
                player?.let { player -> it.start(player) }
        }
    }
}
