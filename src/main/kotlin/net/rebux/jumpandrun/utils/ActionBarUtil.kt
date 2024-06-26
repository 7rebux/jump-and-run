package net.rebux.jumpandrun.utils

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player

object ActionBarUtil {

  fun Player.sendActionBar(message: String) {
    this.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(message))
  }
}
