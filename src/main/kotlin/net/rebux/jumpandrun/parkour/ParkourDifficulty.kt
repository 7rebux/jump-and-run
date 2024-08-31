package net.rebux.jumpandrun.parkour

import net.rebux.jumpandrun.config.ParkourConfig

enum class ParkourDifficulty(val displayName: String) {

    EASY(ParkourConfig.Difficulty.easy),
    NORMAL(ParkourConfig.Difficulty.normal),
    HARD(ParkourConfig.Difficulty.hard),
    ULTRA(ParkourConfig.Difficulty.ultra);

    override fun toString() = displayName
}
