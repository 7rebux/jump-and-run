package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.MessagesConfig
import net.rebux.jumpandrun.config.ParkourConfig
import net.rebux.jumpandrun.config.SoundsConfig
import net.rebux.jumpandrun.config.WebhookConfig
import net.rebux.jumpandrun.database.entities.ParkourEntity
import net.rebux.jumpandrun.database.entities.TimeEntity
import net.rebux.jumpandrun.database.models.Times
import net.rebux.jumpandrun.events.ParkourFinishEvent
import net.rebux.jumpandrun.events.ParkourLeaveEvent
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.safeTeleport
import net.rebux.jumpandrun.utils.*
import net.rebux.jumpandrun.utils.TickFormatter.toMessageValue
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class ParkourFinishListener(private val plugin: Plugin) : Listener {

    @EventHandler
    fun onParkourFinish(event: ParkourFinishEvent) {
        if (event.isCancelled) {
            return
        }

        val player = event.player
        val parkour = event.parkour
        val ticks = player.data.parkourData.timer.stop()
        val (time, unit) = TickFormatter.format(ticks)
        var typeOfFinish = FinishType.Regular

        MessageBuilder(MessagesConfig.Event.completed)
            .values(
                mapOf(
                    "name" to parkour.name,
                    "difficulty" to parkour.difficulty.displayName,
                    "time" to time,
                    "unit" to unit.toMessageValue(),
                )
            )
            .buildAndSend(player)

        if (!parkour.times.contains(player.uniqueId) || ticks < parkour.times[player.uniqueId]!!) {
            val globalBest = parkour.times.values.minOrNull()

            if (globalBest == null) {
                typeOfFinish = FinishType.FirstGlobalBest

                MessageBuilder(MessagesConfig.Event.firstGlobalBest)
                    .values(
                        mapOf(
                            "player" to player.name,
                            "name" to parkour.name,
                            "difficulty" to parkour.difficulty.displayName,
                            "time" to time,
                            "unit" to unit.toMessageValue(),
                        )
                    )
                    .buildAndSendGlobally()
                SoundUtil.playSound(SoundsConfig.firstGlobalBest, player)
            } else if (ticks < globalBest) {
                typeOfFinish = FinishType.NewGlobalBest

                val ticksDelta = globalBest - ticks
                val (deltaTime, deltaUnit) = TickFormatter.format(ticksDelta)
                val previousHolders =
                    parkour.times.entries
                        .filter { it.value == globalBest }
                        .mapNotNull { Bukkit.getOfflinePlayer(it.key).name }
                        .joinToString(", ")

                MessageBuilder(MessagesConfig.Event.globalBest)
                    .values(
                        mapOf(
                            "player" to player.name,
                            "name" to parkour.name,
                            "difficulty" to parkour.difficulty.displayName,
                            "holders" to previousHolders,
                            "time" to deltaTime,
                            "unit" to deltaUnit.toMessageValue(),
                        )
                    )
                    .buildAndSendGlobally()
                SoundUtil.playSound(SoundsConfig.newGlobalBest)

                if (WebhookConfig.enabled) {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin) { ->
                        DiscordWebhook.postGlobalBest(
                            player = player,
                            parkour = parkour,
                            previousHolders = previousHolders,
                            time = time,
                            deltaTime = deltaTime
                        )
                    }
                }
            }
            // New personal best
            else {
                typeOfFinish = FinishType.NewPersonalBest
                MessageBuilder(MessagesConfig.Event.personalBest).buildAndSend(player)
                SoundUtil.playSound(SoundsConfig.newPersonalBest, player)
            }

            updateDatabaseEntry(parkour, player, ticks)

            parkour.times[player.uniqueId] = ticks
            refreshScoreboards(parkour)
        }

        EventLogger.log(
            "ParkourFinishEvent",
            "(Type=$typeOfFinish) Player ${player.name} finished ${parkour.id} in $time ${unit.toMessageValue()} ($ticks ticks)",
        )

        if (ParkourConfig.leaveOnFinish) {
            Bukkit.getPluginManager().callEvent(ParkourLeaveEvent(player))
        } else {
            val startLocation = player.data.parkourData.parkour!!.startLocation

            player.data.parkourData.checkpoint = startLocation
            player.data.parkourData.timer.stop()
            player.safeTeleport(startLocation)
        }
    }

    private fun refreshScoreboards(parkour: Parkour) {
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.data.parkourData.parkour != parkour) {
                continue
            }

            player.scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard
            player.scoreboard = ScoreboardUtil.createParkourScoreboard(parkour, player)
        }
    }

    private fun updateDatabaseEntry(parkour: Parkour, player: Player, ticks: Long) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin) { ->
            transaction {
                val parkourEntity =
                    ParkourEntity.findById(parkour.id)
                        ?: error("Parkour ${parkour.id} found in memory but not in database!")
                val existing =
                    TimeEntity.find {
                        (Times.parkour eq parkour.id) and (Times.uuid eq player.uniqueId)
                    }

                existing.forEach(TimeEntity::delete)

                TimeEntity.new {
                    this.uuid = player.uniqueId
                    this.time = ticks
                    this.date = LocalDateTime.now()
                    this.parkour = parkourEntity
                }
            }
        }
    }

    private enum class FinishType {
        FirstGlobalBest,
        NewGlobalBest,
        NewPersonalBest,
        Regular,
    }
}
