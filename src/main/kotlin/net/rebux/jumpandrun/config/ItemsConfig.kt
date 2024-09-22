package net.rebux.jumpandrun.config

import org.bukkit.Material

object ItemsConfig : CustomConfiguration("items.yml") {

    fun isEnabled(item: String): Boolean {
        return config.getBoolean("$item.enabled")
    }

    fun getName(item: String): String {
        return config.getString("$item.name")
            ?: error("Item name for item $item not found!")
    }

    fun getMaterial(item: String): Material {
        return config.getString("$item.material")?.let(Material::getMaterial)
            ?: error("Material for $item not found!")
    }

    fun getSlot(item: String): Int {
        return config.getInt("$item.slot")
    }
}
