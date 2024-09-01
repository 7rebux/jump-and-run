package net.rebux.jumpandrun.api

import net.rebux.jumpandrun.config.MenuConfig
import org.bukkit.Material

enum class MenuCategory(val material: Material, val displayName: String) {

    All(MenuConfig.Category.All.material, MenuConfig.Category.All.name),
    Easy(MenuConfig.Category.Easy.material, MenuConfig.Category.Easy.name),
    Normal(MenuConfig.Category.Normal.material, MenuConfig.Category.Normal.name),
    Hard(MenuConfig.Category.Hard.material, MenuConfig.Category.Hard.name),
    Ultra(MenuConfig.Category.Ultra.material, MenuConfig.Category.Ultra.name);

    override fun toString() = displayName
}
