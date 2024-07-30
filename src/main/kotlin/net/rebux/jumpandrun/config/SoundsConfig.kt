package net.rebux.jumpandrun.config

object SoundsConfig : CustomConfiguration("sounds.yml") {

  val firstGlobalBest = config.getString("firstGlobalBest")
  val newPersonalBest = config.getString("newPersonalBest")
  val checkpoint      = config.getString("checkpoint")
  val resetBlock      = config.getString("resetBlock")
  val timerStart      = config.getString("timerStart")
  val resetHeight     = config.getString("resetHeight")
  val newGlobalBest   = config.getString("newGlobalBest")
}
