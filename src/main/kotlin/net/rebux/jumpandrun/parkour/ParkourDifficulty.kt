package net.rebux.jumpandrun.parkour

import net.rebux.jumpandrun.config.ParkourConfig

// TODO: This should be more configurable, custom entries and so on
enum class ParkourDifficulty(private val displayName: String) {

    EASY    (ParkourConfig.Difficulty.easy),
    MEDIUM  (ParkourConfig.Difficulty.medium),
    HARD    (ParkourConfig.Difficulty.hard),
    ULTRA   (ParkourConfig.Difficulty.ultra);

    override fun toString() = displayName
}
