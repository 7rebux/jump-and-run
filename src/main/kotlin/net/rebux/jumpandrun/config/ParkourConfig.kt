package net.rebux.jumpandrun.config

import org.bukkit.Material

// TODO: Add documentation for what each option does
object ParkourConfig : CustomConfiguration("parkour.yml") {

    val resetHeight = config.getInt("resetHeight")
    val gameMode = config.getString("gameMode")
        ?: error("Parkour game mode could not be found!")
    val leaveOnFinish = config.getBoolean("leaveOnFinish")
    val spawnOnLeave = config.getBoolean("spawnOnLeave")
    val eventLogging = config.getBoolean("eventLogging")

    internal object Feature {
        val resetBlock = config.getBoolean("feature.resetBlock")
        val checkpoint = config.getBoolean("feature.checkpoint")
    }

    internal object Block {
        val finish = config.getString("block.finish")?.let(Material::getMaterial)
            ?: error("Material not found for block.finish!")

        val reset = config.getString("block.reset")?.let(Material::getMaterial)
            ?: error("Material not found for block.reset!")

        val checkpoint = config.getString("block.checkpoint")?.let(Material::getMaterial)
            ?: error("Material not found for block.checkpoint!")
    }

    internal object Difficulty {
        val easy = config.getString("difficulty.easy")
            ?: error("Difficulty not found for easy!")
        val normal = config.getString("difficulty.normal")
            ?: error("Difficulty not found for normal!")
        val hard = config.getString("difficulty.hard")
            ?: error("Difficulty not found for hard!")
        val ultra = config.getString("difficulty.ultra")
            ?: error("Difficulty not found for ultra!")
    }

    internal object Scoreboard {
        val title = config.getString("scoreboard.title")
            ?: error("Scoreboard title not found!")
        val subtitle = config.getString("scoreboard.subtitle")
            ?: error("Scoreboard subtitle not found!")
        val personalBestHeader = config.getString("scoreboard.personalBestHeader")
            ?: error("Scoreboard personalBestHeader not found!")
        val personalBest = config.getString("scoreboard.personalBest")
            ?: error("Scoreboard personalBest not found!")
        val personalBestFooter = config.getString("scoreboard.personalBestFooter")
            ?: error("Scoreboard personalBestFooter not found!")
        val topTimesHeader = config.getString("scoreboard.topTimesHeader")
            ?: error("Scoreboard topTimesHeader not found!")
        val topTimesEntry = config.getString("scoreboard.topTimesEntry")
            ?: error("Scoreboard topTimesEntry not found!")
        val topTimesFooter = config.getString("scoreboard.topTimesFooter")
            ?: error("Scoreboard topTimesFooter not found!")
    }
}
