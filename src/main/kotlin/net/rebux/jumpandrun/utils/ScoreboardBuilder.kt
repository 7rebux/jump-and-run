package net.rebux.jumpandrun.utils

import org.bukkit.Bukkit
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard

class ScoreboardBuilder {
    private var title = ""
    private val lines = mutableListOf<String>()

    fun title(title: String) = apply { this.title = title }

    fun appendLine(line: String) = apply { this.lines.add(line) }

    internal fun build(): Scoreboard {
        val scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard
        val objective = scoreboard.registerObjective(title)

        objective.displaySlot = DisplaySlot.SIDEBAR
        lines.reversed().withIndex().forEach { (i, line) -> objective.getScore(line).score = i }

        return scoreboard
    }
}

// Contains a fallback for older versions
private fun Scoreboard.registerObjective(name: String): Objective {
    return try {
        this.registerNewObjective(name, Criteria.DUMMY, name)
    } catch (_: NoClassDefFoundError) {
        this.registerNewObjective(name, "dummy")
    }
}

fun scoreboard(builder: ScoreboardBuilder.() -> Unit): Scoreboard =
    ScoreboardBuilder().apply(builder).build()
