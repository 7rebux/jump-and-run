package net.rebux.jumpandrun.config

import org.bukkit.GameMode
import org.bukkit.Material

object ParkourConfig : CustomConfiguration("parkour.yml") {

    /** Specifies the y position at which the player should be reset */
    val resetHeight = config.getInt("resetHeight")

    /** Specifies the game mode in a parkour */
    val gameMode = config.getString("gameMode")?.let(GameMode::valueOf)
        ?: error("Parkour game mode could not be found!")

    /** Whether the player should leave parkour mode after finishing a parkour */
    val leaveOnFinish = config.getBoolean("leaveOnFinish")

    /** Whether the player should run the spawn command when leaving parkour mode */
    val spawnOnLeave = config.getBoolean("spawnOnLeave")

    /** Whether parkour events should be logged in the server console */
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
