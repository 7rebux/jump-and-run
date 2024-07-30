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

    println("Sound value of $sound=${Sound.valueOf(sound)}")
    player.playSound(player.location, Sound.valueOf(sound), 1.0F, 1.0F)
  }

  fun playSound(sound: String) {
    Bukkit.getOnlinePlayers().forEach { player ->
      playSound(sound, player)
    }
  }
}
