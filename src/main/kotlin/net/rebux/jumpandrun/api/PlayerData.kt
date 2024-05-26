package net.rebux.jumpandrun.api

data class PlayerData(
    var parkourData: ParkourData = ParkourData(),
    var practiceData: PracticeData = PracticeData(),
) {

    fun isInParkour() = parkourData.parkour != null

    fun isInPracticeMode() = practiceData.startLocation != null
}
