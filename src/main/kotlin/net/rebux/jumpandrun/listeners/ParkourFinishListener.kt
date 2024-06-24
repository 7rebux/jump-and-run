package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.Plugin
import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.database.entities.ParkourEntity
import net.rebux.jumpandrun.database.entities.TimeEntity
import net.rebux.jumpandrun.database.models.Times
import net.rebux.jumpandrun.events.ParkourFinishEvent
import net.rebux.jumpandrun.msgTemplate
import net.rebux.jumpandrun.msgTemplateGlobal
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.utils.TickFormatter
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Sound
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

    player.msgTemplate("parkour.completed", mapOf(
      "name" to parkour.name,
      "time" to TickFormatter.format(ticks))
    )

    if (!parkour.times.contains(player.uniqueId) ||
      ticks < parkour.times[player.uniqueId]!!) {

      val globalBest = parkour.times.values.minOrNull()

      // First global best
      if (globalBest == null) {
        player.msgTemplate("parkour.firstGlobalBest")
        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F)
      }
      // New global best
      else if (ticks < globalBest) {
        val ticksDelta = globalBest - ticks
        val previousHolders = parkour.times.entries
          .filter { it.value == globalBest }
          .mapNotNull { Bukkit.getOfflinePlayer(it.key).name }
          .joinToString(", ")

        msgTemplateGlobal("parkour.globalBest", mapOf(
          "player" to player.name,
          "name" to parkour.name,
          "holders" to previousHolders,
          "time" to TickFormatter.format(ticksDelta))
        )
        Bukkit.getOnlinePlayers().forEach {
          it.playSound(it.location, Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F)
        }
      }
      // New personal best
      else {
        player.msgTemplate("parkour.personalBest")
        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F)
      }

      updateDatabaseEntry(parkour, player, ticks)
      parkour.times[player.uniqueId] = ticks
    }

    // TODO: Use cache for previous GameMode or new config entry
    player.gameMode = GameMode.SURVIVAL

    // TODO: Config entry for what happens after finishing parkour
    // TODO: For example: Run command, Restart parkour
    player.performCommand("spawn")
    Bukkit.getPluginManager().callEvent(PlayerCommandPreprocessEvent(player, "/spawn"))
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
