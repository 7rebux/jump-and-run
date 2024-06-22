package net.rebux.jumpandrun.utils

object VersionCompatability {

  fun isLegacy(): Boolean {
    try {
      Class.forName("net.minecraft.server.v1_8_R3.ItemStack")
      return true
    } catch (_: Exception) {
      return false
    }
  }
}
