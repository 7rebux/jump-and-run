package net.rebux.jumpandrun.config

import org.bukkit.Sound

object SoundsConfig : CustomConfiguration("sounds.yml") {

    val firstGlobalBest = config.getString("firstGlobalBest")?.let(::getSoundOrNull)
        ?: error("Sound for firstGlobalBest not found!")
    val newPersonalBest = config.getString("newPersonalBest")?.let(::getSoundOrNull)
        ?: error("Sound for newPersonalBest not found!")
    val checkpoint = config.getString("checkpoint")?.let(::getSoundOrNull)
        ?: error("Sound for checkpoint not found!")
    val resetBlock = config.getString("resetBlock")?.let(::getSoundOrNull)
        ?: error("Sound for resetBlock not found!")
    val timerStart = config.getString("timerStart")?.let(::getSoundOrNull)
        ?: error("Sound for timerStart not found!")
    val resetHeight = config.getString("resetHeight")?.let(::getSoundOrNull)
        ?: error("Sound for resetHeight not found!")
    val newGlobalBest = config.getString("newGlobalBest")?.let(::getSoundOrNull)
        ?: error("Sound for newGlobalBest not found!")
}

private fun getSoundOrNull(name: String): Sound? {
    return Sound.values().firstOrNull { it.name == name }
}
