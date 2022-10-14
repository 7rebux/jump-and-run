package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Main
import net.rebux.jumpandrun.item.impl.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent

object InteractionListener: Listener {

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

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val item = event.currentItem
        val player = event.whoClicked as? Player // safe cast (null if whoClicked is not a player)

        // TODO("check action")

        // check if item is valid
        if (item.type == Material.AIR || item?.itemMeta?.displayName == null)
            return

        // start parkour
        Main.instance.parkourManager.parkours.forEach {
            if (it.name.equals(ChatColor.stripColor(item.itemMeta.displayName), true))
                player?.let { player -> it.start(player) }
        }
    }
}
