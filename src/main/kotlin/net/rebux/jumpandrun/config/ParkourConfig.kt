package net.rebux.jumpandrun.config

import org.bukkit.Material

object ParkourConfig : CustomConfiguration("parkour.yml") {

  val resetHeight = config.getInt("resetHeight")
  val gameMode = config.getString("gameMode")!!
  val leaveOnFinish = config.getBoolean("leaveOnFinish")
  val spawnOnLeave = config.getBoolean("spawnOnLeave")

  internal object Feature {
    val resetBlock = config.getBoolean("feature.resetBlock")
    val checkpoint = config.getBoolean("feature.checkpoint")
  }

  // TODO: Material parsing should not happen here?
  internal object Block {
    val finish = Material.getMaterial(config.getString("block.finish") ?: "GOLD_BLOCK")
      ?: error("Material not found for finish block!")
    val reset = Material.getMaterial(config.getString("block.reset") ?: "REDSTONE_BLOCK")
      ?: error("Material not found for reset block!")
    val checkpoint = Material.getMaterial(config.getString("block.checkpoint") ?: "IRON_BLOCK")
      ?: error("Material not found for checkpoint block!")
  }

  internal object Difficulty {
    val easy = config.getString("difficulty.easy")!!
    val normal = config.getString("difficulty.normal")!!
    val hard = config.getString("difficulty.hard")!!
    val ultra = config.getString("difficulty.ultra")!!
  }

  internal object Scoreboard {
    val title = config.getString("scoreboard.title")!!
    val subtitle = config.getString("scoreboard.subtitle")!!
    val personalBestHeader = config.getString("scoreboard.personalBestHeader")!!
    val personalBest = config.getString("scoreboard.personalBest")!!
    val personalBestFooter = config.getString("scoreboard.personalBestFooter")!!
    val topTimesHeader = config.getString("scoreboard.topTimesHeader")!!
    val topTimesEntry = config.getString("scoreboard.topTimesEntry")!!
    val topTimesFooter = config.getString("scoreboard.topTimesFooter")!!
  }
}
