package net.rebux.jumpandrun

import net.minecraft.server.v1_8_R3.IChatBaseComponent
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent
import java.util.logging.Level

private val plugin = Instance.plugin
private val prefix = plugin.config.getString("messages.prefix")

val Player.data
    get() = plugin.playerData[this.uniqueId]
        ?: error("Player data not found for ${this.uniqueId}")

fun template(name: String, values: Map<String, Any> = mapOf()): String {
    val template: String? = plugin.config.getString(name)
    var message: String

    template?.let {
        message = it
        values.forEach { entry -> message = message.replace("{${entry.key}}", entry.value.toString()) }
        return message
    } ?: plugin.logger.log(Level.SEVERE, "Template '${name}' not found!\"")

    return "${ChatColor.RED}Not found"
}

fun CommandSender.msg(message: String) {
    this.sendMessage("$prefix $message")
}

fun Player.msgTemplate(name: String, values: Map<String, Any> = mapOf()) {
    msg(template("messages.$name", values))
}

fun CommandSender.msgTemplate(name: String, values: Map<String, Any> = mapOf()) {
    msg(template("messages.$name", values))
}

fun msgTemplateGlobal(name: String, values: Map<String, Any> = mapOf()) {
    Bukkit.broadcastMessage("$prefix ${template("messages.$name", values)}")
}

fun Player.sendActionBar(text: String) {
    (this as CraftPlayer).handle.playerConnection.sendPacket(
        PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"$text\"}"), 2)
    )
}

fun PlayerMoveEvent.hasMoved(): Boolean {
    return this.from.x != this.to.x || this.from.y != this.to.y || this.from.z != this.to.z
}
