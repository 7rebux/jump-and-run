package net.rebux.jumpandrun.parkour

import net.rebux.jumpandrun.Main
import net.rebux.jumpandrun.events.ParkourFinishEvent
import net.rebux.jumpandrun.item.impl.CheckpointItem
import net.rebux.jumpandrun.item.impl.LeaveItem
import net.rebux.jumpandrun.item.impl.RestartItem
import net.rebux.jumpandrun.sql.SQLQueries
import net.rebux.jumpandrun.utils.InventoryUtil
import net.rebux.jumpandrun.utils.TimeUtil
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerCommandPreprocessEvent

@Suppress("SpellCheckingInspection")
class Parkour(
    val id: Int,
    val name: String,
    val builder: String,
    val difficulty: Difficulty,
    val material: Material,
    val location: Location,
) {

    private val plugin = Main.instance

    fun start(player: Player) {
        // teleport
        player.teleport(location)

        // save & clear inventory
        InventoryUtil.saveInventory(player)
        player.inventory.clear()

        // add items
        player.inventory.setItem(0, CheckpointItem().getItemStack())
        player.inventory.setItem(1, RestartItem().getItemStack())
        //player.inventory.setItem(4, HiderItem().getItemStack())
        player.inventory.setItem(8, LeaveItem().getItemStack())

        plugin.active[player] = this
        plugin.checkpoints[player] = location
        plugin.times[player] = player.ticksLived
    }

    fun finish(player: Player) {
        val ticksNeeded = player.ticksLived - plugin.times[player]!!

        player.sendMessage("${Main.PREFIX} Du hast das Jump and Run ${ChatColor.GREEN}$name ${ChatColor.GRAY}in ${ChatColor.GREEN}${TimeUtil.ticksToTime(ticksNeeded)} ${ChatColor.GRAY}geschafft")

        // call finish event
        Bukkit.getPluginManager().callEvent(ParkourFinishEvent(player))

        // handle time
        Bukkit.getScheduler().runTaskAsynchronously(plugin) {
            if (!SQLQueries.hasPersonalBestTime(player, this) || ticksNeeded < SQLQueries.getPersonalBestTime(player, this)) {
                // first global best
                if (!SQLQueries.hasGlobalBestTime(this)) {
                    player.sendMessage("${Main.PREFIX} Du hast die ${ChatColor.GREEN}Erste Globale Bestzeit ${ChatColor.GRAY}erzielt!")
                    player.playSound(player.location, Sound.LEVEL_UP, 1.0F, 1.0F)
                }

                // new global best
                else if (ticksNeeded < SQLQueries.getGlobalBestTimes(this).second) {
                    val timeDif = SQLQueries.getGlobalBestTimes(this).second - ticksNeeded
                    val recordHolders = SQLQueries.getGlobalBestTimes(this).first
                        .joinToString(", ") { Bukkit.getOfflinePlayer(it).name }

                    Bukkit.broadcastMessage("${Main.PREFIX} ${ChatColor.GREEN}${player.name} ${ChatColor.GRAY}hat die Bestzeit bei ${ChatColor.GREEN}$name ${ChatColor.GRAY}von ${ChatColor.GREEN}$recordHolders ${ChatColor.GRAY}um ${ChatColor.GREEN}${TimeUtil.ticksToTime(timeDif)} ${ChatColor.GRAY}geschlagen!")
                    Bukkit.getOnlinePlayers().forEach { it.playSound(player.location, Sound.ANVIL_LAND, 1.0F, 1.0F) }
                }

                // new personal best
                else {
                    player.sendMessage("${Main.PREFIX} Du hast eine neue ${ChatColor.GREEN}persönliche Bestzeit ${ChatColor.GRAY}erzielt!")
                    player.playSound(player.location, Sound.LEVEL_UP, 1.0F, 1.0F)
                }

                // update best time
                SQLQueries.updateBestTime(ticksNeeded, player, this)
            }
        }

        // teleport to spawn
        player.performCommand("spawn")
        Bukkit.getPluginManager().callEvent(PlayerCommandPreprocessEvent(player, "/spawn"))
    }
}
