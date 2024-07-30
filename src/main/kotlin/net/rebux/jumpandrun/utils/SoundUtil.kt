package net.rebux.jumpandrun.utils

import net.rebux.jumpandrun.config.SoundsConfig
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player

object SoundUtil {

  fun playSound(sound: String, player: Player) {
    if (!SoundsConfig.isEnabled(sound)) {
      return
    }

    player.playSound(player.location, Sound.valueOf(SoundsConfig.getSound(sound)), 1.0F, 1.0F)
  }

  fun playSound(sound: String) {
    Bukkit.getOnlinePlayers().forEach { player ->
      playSound(sound, player)
    }
  }
}
