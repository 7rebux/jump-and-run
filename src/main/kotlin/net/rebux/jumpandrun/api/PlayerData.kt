package net.rebux.jumpandrun.api

data class PlayerData(
    val parkourData: ParkourData = ParkourData(),
    val practiceData: PracticeData = PracticeData(),
    var playersHidden: Boolean = false,
    val menuState: PlayerMenuState = PlayerMenuState(),
) {

    val inParkour: Boolean
        get() = this@PlayerData.parkourData.parkour != null

    val inPractice: Boolean
        get() = practiceData.startLocation != null
}
