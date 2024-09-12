package net.rebux.jumpandrun.parkour

import org.bukkit.ChatColor

enum class ParkourDifficulty(val color: ChatColor, val displayName: String) {

    EASY(ChatColor.GREEN, "Easy"),
    NORMAL(ChatColor.GOLD, "Normal"),
    HARD(ChatColor.RED, "Hard"),
    ULTRA(ChatColor.DARK_BLUE, "Ultra");

    val coloredName = "$color$displayName"
}
