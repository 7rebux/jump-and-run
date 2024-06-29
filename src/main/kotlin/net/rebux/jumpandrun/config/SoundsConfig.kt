package net.rebux.jumpandrun.config

// TODO: Null values handling
object SoundsConfig : CustomConfiguration("sounds.yml") {

  val firstGlobalBest = config.getString("firstGlobalBest.sound")!!
  val newPersonalBest = config.getString("newPersonalBest.sound")!!
  val checkpoint = config.getString("checkpoint.sound")!!
  val resetBlock = config.getString("resetBlock.sound")!!
  val timerStart = config.getString("timerStart.sound")!!
  val resetHeight = config.getString("resetHeight.sound")!!
  val newGlobalBest = config.getString("newGlobalBest.sound")!!


  fun isEnabled(sound: String): Boolean {
    return config.getBoolean("$sound.enabled")
  }
}
