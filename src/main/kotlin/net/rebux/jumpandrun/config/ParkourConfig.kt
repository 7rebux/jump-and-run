package net.rebux.jumpandrun.config

import org.bukkit.Material

object ParkourConfig : CustomConfiguration("parkour.yml") {

  val resetHeight = config.getInt("resetHeight")
  val gameMode = config.getString("gameMode")!!
  val leaveOnFinish = config.getBoolean("leaveOnFinish")

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
    val medium = config.getString("difficulty.medium")!!
    val hard = config.getString("difficulty.hard")!!
    val extreme = config.getString("difficulty.extreme")!!
  }
}