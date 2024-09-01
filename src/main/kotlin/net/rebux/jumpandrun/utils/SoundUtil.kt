package net.rebux.jumpandrun.utils

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player

object SoundUtil {

    fun playSound(sound: Sound, player: Player) {
        player.playSound(player.location, sound, 1.0F, 1.0F)
    }

    fun playSound(sound: Sound) {
        Bukkit.getOnlinePlayers().forEach { player -> playSound(sound, player) }
    }
}
