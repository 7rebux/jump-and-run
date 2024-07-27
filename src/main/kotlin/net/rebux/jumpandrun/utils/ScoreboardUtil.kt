package net.rebux.jumpandrun.utils

import net.rebux.jumpandrun.parkour.Parkour
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Scoreboard

// TODO: Remove scoreboard when leaving parkour
// TODO: Create new configuration file for scoreboard texts
object ScoreboardUtil {

  fun createScoreboard(parkour: Parkour, player: Player) : Scoreboard {
    val scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard
    val objective = scoreboard.registerNewObjective("parkourRecords", Criteria.DUMMY, parkour.name)
    val bestTimes = parkour.times.entries
      .sortedBy { it.value }
      .take(10)
      .reversed()

    objective.displaySlot = DisplaySlot.SIDEBAR

    // TODO: Colors
    objective.getScore("Deine Bestzeit").score = 14
    objective.getScore(parkour.times[player.uniqueId]?.toString() ?: "--:--:--").score = 13 // TODO: Format correctly
    objective.getScore("").score = 12
    objective.getScore("------ Top 10 ------").score = 11

    for (i in 10..1) {
      val bestTime = bestTimes.getOrNull(i - 1)

      bestTime?.run {
        objective.getScore("${bestTime.value} ${Bukkit.getOfflinePlayer(bestTime.key)}").score = i // TODO: Format correctly
      }

      // TODO: Maybe add empty slots to scoreboard as well
    }

    objective.getScore("--------------------").score = 0

    return scoreboard
  }
}
