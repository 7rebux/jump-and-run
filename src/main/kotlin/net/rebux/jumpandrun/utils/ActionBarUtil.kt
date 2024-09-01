package net.rebux.jumpandrun.utils

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_8_R3.IChatBaseComponent
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

object ActionBarUtil {

    fun Player.sendActionBar(message: String) {
        try {
            this.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(message))
        } catch (_: NoSuchMethodError) {
            // Fallback for 1.8 servers
            (this as CraftPlayer)
                .handle
                .playerConnection
                .sendPacket(
                    PacketPlayOutChat(
                        IChatBaseComponent.ChatSerializer.a("{\"text\":\"$message\"}"), 2))
        }
    }
}
