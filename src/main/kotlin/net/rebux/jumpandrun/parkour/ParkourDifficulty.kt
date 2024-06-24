package net.rebux.jumpandrun.parkour

import net.rebux.jumpandrun.Instance
import org.bukkit.ChatColor

enum class ParkourDifficulty(private val displayName: String, private val color: ChatColor) {

    // TODO: Difficulties should be configurable and names should not be loaded here
    EASY    (Instance.plugin.config.getString("difficulty.easy")!!, ChatColor.GREEN),
    MEDIUM  (Instance.plugin.config.getString("difficulty.medium")!!, ChatColor.GOLD),
    HARD    (Instance.plugin.config.getString("difficulty.hard")!!, ChatColor.RED),
    EXTREME (Instance.plugin.config.getString("difficulty.extreme")!!, ChatColor.DARK_PURPLE);

    override fun toString() = "$color$displayName"
}
