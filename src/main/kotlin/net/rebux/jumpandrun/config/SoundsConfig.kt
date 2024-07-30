package net.rebux.jumpandrun.config

object SoundsConfig : CustomConfiguration("sounds.yml") {

  fun getSound(sound: String): String {
    return config.getString("$sound.sound")!!
  }

  fun isEnabled(sound: String): Boolean {
    return config.getBoolean("$sound.enabled")
  }
}
