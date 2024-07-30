package net.rebux.jumpandrun.utils

import org.bukkit.Bukkit
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Scoreboard

class ScoreboardBuilder {
  private val lines = mutableListOf<String>()
  var title = ""

  fun title(title: String) = apply { this.title = title }

  fun appendLine(line: String) = apply { this.lines.add(line) }

  internal fun build(): Scoreboard {
    val scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard
    val objective = scoreboard.registerNewObjective(title, Criteria.DUMMY, title)

    objective.displaySlot = DisplaySlot.SIDEBAR
    lines.withIndex().reversed().forEach { (i, line) ->
      objective.getScore(line).score = i
    }

    return scoreboard
  }
}

fun scoreboard(builder: ScoreboardBuilder.() -> Unit): Scoreboard =
  ScoreboardBuilder().apply(builder).build()