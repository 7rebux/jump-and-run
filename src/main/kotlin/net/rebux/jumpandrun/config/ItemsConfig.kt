package net.rebux.jumpandrun.config

object ItemsConfig : CustomConfiguration("items.yml") {

    fun isEnabled(item: String): Boolean {
        return config.getBoolean("$item.enabled")
    }

    fun getName(item: String): String {
        return config.getString("$item.name")
            ?: error("Item name for item $item not found!")
    }

    fun getMaterial(item: String): String {
        return config.getString("$item.material")
            ?: error("Material for item $item not found!")
    }

    fun getSlot(item: String): Int {
        return config.getInt("$item.slot")
    }
}
