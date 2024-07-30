package net.rebux.jumpandrun.utils

import net.rebux.jumpandrun.config.MessagesConfig
import net.rebux.jumpandrun.config.ParkourConfig
import net.rebux.jumpandrun.parkour.Parkour
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Scoreboard
import java.util.concurrent.TimeUnit

object ScoreboardUtil {

  private val config = ParkourConfig.Scoreboard

  fun createParkourScoreboard(parkour: Parkour, player: Player) : Scoreboard {
    return scoreboard {
      title(parkour.name)

      appendLine("Line1")
      appendLine("Line2")
      appendLine("Line3 " + player.name)
    }

    val scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard
    val title = MessageBuilder(config.title)
      .values(mapOf("name" to parkour.name))
      .prefix(false)
      .buildSingle()
    val objective = scoreboard.registerNewObjective("parkourRecords", Criteria.DUMMY, title)
    val personalBest = parkour.times[player.uniqueId]?.let(TickFormatter::format)
    val unitString = personalBest?.let { if (it.second == TimeUnit.SECONDS) MessagesConfig.Timer.Unit.seconds else MessagesConfig.Timer.Unit.minutes }
    val bestTimes = parkour.times.entries
      .sortedBy { it.value }
      .take(10)
      .reversed()

    objective.displaySlot = DisplaySlot.SIDEBAR

    objective.getScore(MessageBuilder(config.personalBestHeader).prefix(false).buildSingle()).score = 14
    objective.getScore(
      MessageBuilder(config.personalBest)
        .values(
          mapOf(
            "time" to (personalBest?.first ?: "--:--:--"),
            "unit" to (unitString ?: "")))
        .prefix(false)
        .buildSingle()).score = 13
    objective.getScore(MessageBuilder(config.personalBestFooter).prefix(false).buildSingle()).score = 12

    objective.getScore(MessageBuilder(config.topTimesHeader).prefix(false).buildSingle()).score = 11
    // TODO: Maybe add empty slots to scoreboard as well
    for (i in 10 downTo 1) {
      bestTimes.getOrNull(i - 1)?.run {
        objective.getScore(
          MessageBuilder(config.topTimesEntry)
            .values(
              mapOf(
                "time" to TickFormatter.format(this.value).first,
                "player" to Bukkit.getOfflinePlayer(this.key).name!!))
            .prefix(false)
            .buildSingle()).score = i
      }
    }
    objective.getScore(MessageBuilder(config.topTimesFooter).prefix(false).buildSingle()).score = 0

    return scoreboard
  }
}
