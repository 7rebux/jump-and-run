package net.rebux.jumpandrun.config

object ItemsConfig : CustomConfiguration("items.yml") {

  fun isEnabled(item: String): Boolean {
    return config.getBoolean("$item.enabled")
  }

  fun getName(item: String): String {
    return config.getString("$item.name") ?: "Unnamed"
  }

  // TODO: Return material instead of string
  fun getMaterial(item: String): String {
    return config.getString("$item.material") ?: "BARRIER"
  }

  fun getSlot(item: String): Int {
    return config.getInt("$item.slot")
  }
}
