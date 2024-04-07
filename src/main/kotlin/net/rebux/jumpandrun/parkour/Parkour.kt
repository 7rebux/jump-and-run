package net.rebux.jumpandrun.parkour

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
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

class Parkour(
    val id: Int,
    val name: String,
    val builder: String,
    val difficulty: ParkourDifficulty,
    val material: Material,
    val location: Location,
    // TODO: Times should be a Map of uuid -> time
    var times: MutableList<Time> = mutableListOf()
) {
    data class Time(val uuid: UUID, var ticks: Int, var version: MinecraftVersion)

    private val plugin = Instance.plugin

    fun resetTime(uuid: UUID) {
        transaction {
            TimeEntity.all()
                .find { it.parkour.id.value == this@Parkour.id && it.uuid == uuid }?.delete()
        }
        times.removeIf { it.uuid == uuid }
    }

    fun resetTimes() {
        transaction {
            TimeEntity.all()
                .filter { it.parkour.id.value == this@Parkour.id }
                .forEach(TimeEntity::delete)
        }
        times.clear()
    }

    fun start(player: Player) {
        player.teleport(location)
        player.gameMode = GameMode.ADVENTURE

        InventoryUtil.saveInventory(player)
        player.inventory.clear()
        player.inventory.setItem(0, ItemRegistry.getItemStack(CheckpointItem.id))
        player.inventory.setItem(1, ItemRegistry.getItemStack(RestartItem.id))
        player.inventory.setItem(8, ItemRegistry.getItemStack(LeaveItem.id))

        player.data.apply {
            parkour = this@Parkour
            checkpoint = location
        }
    }

    fun finish(player: Player) {
        val ticksNeeded = player.data.timer.stop()
        val globalBest = times.minOfOrNull { it.ticks }
        val bar: String = template(
            "timer.bar",
            mapOf("time" to TimeUtil.formatTicks(ticksNeeded))
        )

        player.msgTemplate("parkour.completed", mapOf(
            "name" to name,
            "time" to TimeUtil.formatTicks(ticksNeeded))
        )

        // call finish event
        Bukkit.getPluginManager().callEvent(ParkourFinishEvent(player))

        // handle time
        if (!times.any { it.uuid == player.uniqueId } || ticksNeeded < times.first { it.uuid == player.uniqueId }.ticks) {
            // first global best
            if (globalBest == null) {
                player.msgTemplate("parkour.firstGlobalBest")
                player.playSound(player.location, Sound.LEVEL_UP, 1.0F, 1.0F)
            }

            // new global best
            else if (ticksNeeded < globalBest) {
                val delta = globalBest - ticksNeeded
                val holders = times
                    .filter { it.ticks == globalBest }
                    .joinToString(", ") { Bukkit.getOfflinePlayer(it.uuid).name }

                msgTemplateGlobal("parkour.globalBest", mapOf(
                    "player" to player.name,
                    "name" to name,
                    "holders" to holders,
                    "time" to TimeUtil.formatTicks(delta))
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
                        times.first { it.uuid == player.uniqueId }.ticks = ticksNeeded
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
