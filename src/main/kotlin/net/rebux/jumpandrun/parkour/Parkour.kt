package net.rebux.jumpandrun.parkour

import net.rebux.jumpandrun.*
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

class Parkour(
    val id: Int,
    val name: String,
    val builder: String,
    val difficulty: Difficulty,
    val material: Material,
    val location: Location,
) {

    private val plugin = Instance.plugin

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

        player.msgTemplate("parkour.completed", mapOf(
            "name" to name,
            "time" to TimeUtil.ticksToTime(ticksNeeded))
        )

        // call finish event
        Bukkit.getPluginManager().callEvent(ParkourFinishEvent(player))

        // handle time
        Bukkit.getScheduler().runTaskAsynchronously(plugin) {
            if (!SQLQueries.hasPersonalBestTime(player, this) || ticksNeeded < SQLQueries.getPersonalBestTime(player, this)) {
                // first global best
                if (!SQLQueries.hasGlobalBestTime(this)) {
                    player.msgTemplate("parkour.firstGlobalBest")
                    player.playSound(player.location, Sound.LEVEL_UP, 1.0F, 1.0F)
                }

                // new global best
                else if (ticksNeeded < SQLQueries.getGlobalBestTimes(this).second) {
                    val timeDif = SQLQueries.getGlobalBestTimes(this).second - ticksNeeded
                    val recordHolders = SQLQueries.getGlobalBestTimes(this).first
                        .joinToString(", ") { Bukkit.getOfflinePlayer(it).name }

                    msgTemplateGlobal("parkour.globalBest", mapOf(
                        "player" to player.name,
                        "name" to name,
                        "holders" to recordHolders,
                        "time" to TimeUtil.ticksToTime(timeDif))
                    )
                    Bukkit.getOnlinePlayers().forEach { it.playSound(player.location, Sound.ANVIL_LAND, 1.0F, 1.0F) }
                }

                // new personal best
                else {
                    player.msgTemplate("parkour.personalBest")
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
