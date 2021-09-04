package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Main
import net.rebux.jumpandrun.inventories.ParkourInventory
import net.rebux.jumpandrun.parkour.Items
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class InteractionListener: Listener {
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (event.item != null && event.item.itemMeta.displayName != null) {
            when (event.item.itemMeta.displayName) {
                "${ChatColor.AQUA}${ChatColor.BOLD}Jump&Run's" ->
                    event.player.openInventory(ParkourInventory(event.player).inventory)
                Items.getCheckpointItem().itemMeta.displayName ->
                    event.player.teleport(Main.instance.playerCheckpoints[event.player]!!.second)
                Items.getLeaveItem().itemMeta.displayName ->
                    Main.instance.playerCheckpoints[event.player]!!.first.quit(event.player)
            }
        }
    }
}