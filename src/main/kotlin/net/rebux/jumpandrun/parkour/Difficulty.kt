package net.rebux.jumpandrun.parkour

import net.rebux.jumpandrun.Instance
import org.bukkit.ChatColor

enum class Difficulty(
    val id: Int,
    private val displayName: String,
    private val color: ChatColor
) {

    EASY    (0, Instance.plugin.config.getString("difficulty.easy"), ChatColor.GREEN),
    MEDIUM  (1, Instance.plugin.config.getString("difficulty.medium"), ChatColor.GOLD),
    HARD    (2, Instance.plugin.config.getString("difficulty.hard"), ChatColor.RED),
    EXTREME (3, Instance.plugin.config.getString("difficulty.extreme"), ChatColor.DARK_PURPLE);

    override fun toString() = "$color$displayName"

    companion object {
        fun getById(id: Int): Difficulty? = values().find { it.id == id }
    }
}
