package net.rebux.jumpandrun.inventory.menu

import net.rebux.jumpandrun.config.MenuConfig
import org.bukkit.ChatColor
import org.bukkit.Material

enum class MenuCategory(val material: Material, val color: ChatColor) {
    All(MenuConfig.Category.All.material, ChatColor.WHITE),
    Easy(MenuConfig.Category.Easy.material, ChatColor.GREEN),
    Normal(MenuConfig.Category.Normal.material, ChatColor.GOLD),
    Hard(MenuConfig.Category.Hard.material, ChatColor.RED),
    Ultra(MenuConfig.Category.Ultra.material, ChatColor.DARK_PURPLE);
}
