package net.rebux.jumpandrun

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.logging.Level

private val plugin = Instance.plugin
private val prefix = plugin.config.getString("prefix")

fun error(message: String) {
    plugin.logger.log(Level.SEVERE, message)
}

fun template(name: String, values: Map<String, Any> = mapOf()): String {
    val template: String? = plugin.config.getString(name)
    var message: String

    template?.let {
        message = it
        values.forEach { entry -> message = message.replace("{${entry.key}}", entry.value.toString()) }
        return message
    } ?: error("Template '${name}' not found!")

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
