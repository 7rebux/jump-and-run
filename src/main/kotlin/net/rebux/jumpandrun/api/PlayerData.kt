package net.rebux.jumpandrun.api

import org.bukkit.GameMode

data class PlayerData(
  val parkourData: ParkourData = ParkourData(),
  val practiceData: PracticeData = PracticeData(),
  var playersHidden: Boolean = false,
  var previousGameMode: GameMode? = null
) {

  val inParkour: Boolean
    get() = this@PlayerData.parkourData.parkour != null

  val inPractice: Boolean
    get() = practiceData.startLocation != null
}
