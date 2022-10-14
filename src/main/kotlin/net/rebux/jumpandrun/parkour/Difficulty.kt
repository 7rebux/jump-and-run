package net.rebux.jumpandrun.parkour

import org.bukkit.ChatColor

@Suppress("SpellCheckingInspection")
enum class Difficulty(
    val id: Int,
    private val displayName: String,
    private val color: ChatColor
) {
    EASY    (0, "Einfach",  ChatColor.GREEN),
    MEDIUM  (1, "Mittel",   ChatColor.GOLD),
    HARD    (2, "Schwer",   ChatColor.RED),
    EXTREME (3, "Extrem",   ChatColor.DARK_PURPLE);

    override fun toString() = "$color$displayName"

    companion object {
        fun getById(id: Int): Difficulty? = values().find { it.id == id }
    }
}
