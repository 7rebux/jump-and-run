package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.MessagesConfig
import net.rebux.jumpandrun.config.ParkourConfig
import net.rebux.jumpandrun.config.SoundsConfig
import net.rebux.jumpandrun.database.entities.ParkourEntity
import net.rebux.jumpandrun.database.entities.TimeEntity
import net.rebux.jumpandrun.database.models.Times
import net.rebux.jumpandrun.events.ParkourFinishEvent
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.safeTeleport
import net.rebux.jumpandrun.utils.MessageBuilder
import net.rebux.jumpandrun.utils.SoundUtil
import net.rebux.jumpandrun.utils.TickFormatter
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
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
    val ticks = player.data.timer.stop()
    val (time, unit) = TickFormatter.format(ticks)

    MessageBuilder(MessagesConfig.Event.completed)
      .values(mapOf(
        "name" to parkour.name,
        "time" to time,
        "unit" to unit))
      .buildAndSend(player)

    if (!parkour.times.contains(player.uniqueId) ||
      ticks < parkour.times[player.uniqueId]!!) {

      val globalBest = parkour.times.values.minOrNull()

      // First global best
      if (globalBest == null) {
        MessageBuilder(MessagesConfig.Event.firstGlobalBest)
          .values(mapOf(
            "player" to player.name,
            "name" to parkour.name,
            "time" to time,
            "unit" to unit))
          .buildAndSendGlobally()
        SoundUtil.playSound(SoundsConfig.firstGlobalBest, player)
      }
      // New global best
      else if (ticks < globalBest) {
        val ticksDelta = globalBest - ticks
        val (deltaTime, deltaUnit) = TickFormatter.format(ticksDelta)
        val previousHolders = parkour.times.entries
          .filter { it.value == globalBest }
          .mapNotNull { Bukkit.getOfflinePlayer(it.key).name }
          .joinToString(", ")

        MessageBuilder(MessagesConfig.Event.globalBest)
          .values(mapOf(
            "player" to player.name,
            "name" to parkour.name,
            "holders" to previousHolders,
            "time" to deltaTime,
            "unit" to deltaUnit))
          .buildAndSendGlobally()
        SoundUtil.playSound(SoundsConfig.newGlobalBest)
      }
      // New personal best
      else {
        MessageBuilder(MessagesConfig.Event.personalBest).buildAndSend(player)
        SoundUtil.playSound(SoundsConfig.newPersonalBest, player)
      }

      updateDatabaseEntry(parkour, player, ticks)
      parkour.times[player.uniqueId] = ticks
    }

    if (ParkourConfig.leaveOnFinish) {
      player.performCommand("spawn")
      Bukkit.getPluginManager().callEvent(PlayerCommandPreprocessEvent(player, "/spawn"))
    } else {
      val startLocation = player.data.parkour!!.location

      player.data.checkpoint = startLocation
      player.data.timer.stop()
      player.safeTeleport(startLocation)
    }
  }

  private fun updateDatabaseEntry(
    parkour: Parkour,
    player: Player,
    ticks: Long
  ) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin) { ->
      transaction {
        val parkourEntity = ParkourEntity.findById(parkour.id)
          ?: error("Parkour ${parkour.id} found in memory but not in database!")
        val existing = TimeEntity.find {
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
}