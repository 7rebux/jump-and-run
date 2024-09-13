package net.rebux.jumpandrun.parkour

import org.bukkit.ChatColor
import org.bukkit.Color

enum class ParkourDifficulty(chatColor: ChatColor, val rgbColor: Int, val displayName: String) {

    EASY(ChatColor.GREEN, Color.GREEN.asRGB(), "Easy"),
    NORMAL(ChatColor.GOLD, Color.ORANGE.asRGB(), "Normal"),
    HARD(ChatColor.RED, Color.RED.asRGB(), "Hard"),
    ULTRA(ChatColor.DARK_PURPLE, Color.PURPLE.asRGB(), "Ultra");

    val coloredName = "$chatColor$displayName"
}
