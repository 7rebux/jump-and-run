package net.rebux.jumpandrun.utils

import net.rebux.jumpandrun.config.MessagesConfig
import net.rebux.jumpandrun.config.ParkourConfig
import net.rebux.jumpandrun.parkour.Parkour
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard
import java.util.concurrent.TimeUnit

object ScoreboardUtil {

  private val config = ParkourConfig.Scoreboard

  fun createParkourScoreboard(parkour: Parkour, player: Player) : Scoreboard {
    val personalBest = parkour.times[player.uniqueId]?.let(TickFormatter::format)
    val unitString = personalBest?.let {
      if (it.second == TimeUnit.SECONDS)
        MessagesConfig.Timer.Unit.seconds
      else
        MessagesConfig.Timer.Unit.minutes
    }
    val bestTimes = parkour.times.entries
      .sortedBy { it.value }
      .take(10)

    return scoreboard {
      title(
        MessageBuilder(config.title)
          .values(mapOf("name" to parkour.name))
          .prefix(false)
          .buildSingle()
      )

      appendLine(MessageBuilder(config.personalBestHeader).prefix(false).buildSingle())
      appendLine(
        MessageBuilder(config.personalBest)
          .values(
            mapOf(
              "time" to (personalBest?.first ?: "--:--:--"),
              "unit" to (unitString ?: "")))
          .prefix(false)
          .buildSingle()
      )
      appendLine(MessageBuilder(config.personalBestFooter).prefix(false).buildSingle())
      appendLine(MessageBuilder(config.topTimesHeader).prefix(false).buildSingle())
      bestTimes.forEach {
        appendLine(
          MessageBuilder(config.topTimesEntry)
            .values(
              mapOf(
                "time" to TickFormatter.format(it.value).first,
                "player" to Bukkit.getOfflinePlayer(it.key).name!!))
            .prefix(false)
            .buildSingle()
        )
      }
      appendLine(MessageBuilder(config.topTimesFooter).prefix(false).buildSingle())
    }
  }
}
