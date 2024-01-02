package net.rebux.jumpandrun.parkour

import net.minecraft.server.v1_8_R3.IChatBaseComponent
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import net.rebux.jumpandrun.*
import net.rebux.jumpandrun.database.entities.ParkourEntity
import net.rebux.jumpandrun.database.entities.TimeEntity
import net.rebux.jumpandrun.events.ParkourFinishEvent
import net.rebux.jumpandrun.item.impl.CheckpointItem
import net.rebux.jumpandrun.item.impl.LeaveItem
import net.rebux.jumpandrun.item.impl.RestartItem
import net.rebux.jumpandrun.item.ItemRegistry
import net.rebux.jumpandrun.utils.InventoryUtil
import net.rebux.jumpandrun.utils.TimeUtil
import org.bukkit.*
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class Parkour(
    val id: Int,
    val name: String,
    val builder: String,
    val difficulty: Difficulty,
    val material: Material,
    val location: Location,
    var times: MutableList<ParkourTime> = mutableListOf()
) {
    private val plugin = Instance.plugin

    fun start(player: Player) {
        // teleport
        player.teleport(location)

        // set in adventure mode to prevent glitches with block breaking
        player.gameMode = GameMode.ADVENTURE

        // save & clear inventory
        InventoryUtil.saveInventory(player)
        player.inventory.clear()

        // add items
        player.inventory.setItem(0, ItemRegistry.getItemStack(CheckpointItem.id))
        player.inventory.setItem(1, ItemRegistry.getItemStack(RestartItem.id))
        player.inventory.setItem(8, ItemRegistry.getItemStack(LeaveItem.id))

        plugin.active[player] = this
        plugin.checkpoints[player] = location
        plugin.tickCounters[player] = 0
    }

    fun finish(player: Player) {
        val ticksNeeded = plugin.tickCounters.remove(player)!!
        val globalBest = times.minOfOrNull { it.time }
        val bar: String = template(
            "timer.bar",
            mapOf("time" to TimeUtil.ticksToTime(ticksNeeded))
        )

        player.msgTemplate("parkour.completed", mapOf(
            "name" to name,
            "time" to TimeUtil.ticksToTime(ticksNeeded))
        )

        // display last tick
        (player as CraftPlayer).handle.playerConnection
            .sendPacket(PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"$bar\"}"), 2))

        // call finish event
        Bukkit.getPluginManager().callEvent(ParkourFinishEvent(player))

        // handle time
        if (!times.any { it.uuid == player.uniqueId } || ticksNeeded < times.first { it.uuid == player.uniqueId }.time) {
            // first global best
            if (globalBest == null) {
                player.msgTemplate("parkour.firstGlobalBest")
                player.playSound(player.location, Sound.LEVEL_UP, 1.0F, 1.0F)
            }

            // new global best
            else if (ticksNeeded < globalBest) {
                val delta = globalBest - ticksNeeded
                val holders = times
                    .filter { it.time == globalBest }
                    .joinToString(", ") { Bukkit.getOfflinePlayer(it.uuid).name }

                msgTemplateGlobal("parkour.globalBest", mapOf(
                    "player" to player.name,
                    "name" to name,
                    "holders" to holders,
                    "time" to TimeUtil.ticksToTime(delta))
                )
                Bukkit.getOnlinePlayers().forEach { it.playSound(player.location, Sound.ANVIL_LAND, 1.0F, 1.0F) }
            }

            // new personal best
            else {
                player.msgTemplate("parkour.personalBest")
                player.playSound(player.location, Sound.LEVEL_UP, 1.0F, 1.0F)
            }

            // update best time
            Bukkit.getScheduler().runTaskAsynchronously(plugin) {
                transaction {
                    TimeEntity.all()
                        .find { it.parkour.id.value == this@Parkour.id && it.uuid == player.uniqueId }
                        ?.delete()

                    TimeEntity.new {
                        uuid = player.uniqueId
                        time = ticksNeeded
                        date = LocalDateTime.now()
                        parkour = ParkourEntity.findById(this@Parkour.id)!!
                    }.also {
                        times.first { it.uuid == player.uniqueId }.time = ticksNeeded
                    }
                }
            }
        }

        player.gameMode = GameMode.SURVIVAL

        // teleport to spawn
        player.performCommand("spawn")
        Bukkit.getPluginManager().callEvent(PlayerCommandPreprocessEvent(player, "/spawn"))
    }
}
