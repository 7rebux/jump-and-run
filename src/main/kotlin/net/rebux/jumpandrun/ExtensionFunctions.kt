package net.rebux.jumpandrun

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.logging.Level

private val plugin = Instance.plugin

fun error(message: String) {
    plugin.logger.log(Level.SEVERE, message)
}

fun Player.msgTemplate(name: String, values: Map<String, Any> = mapOf()) {
    val template: String? = plugin.config.getString("messages.$name")
    var message: String

    template?.let {
        message = it
        values.forEach { entry -> message = message.replace("{${entry.key}}", entry.value.toString()) }
        this.sendMessage(plugin.config.getString("prefix") + " $message")
    } ?: error("Template '${name}' not found!")
}

fun CommandSender.msg(message: String) {
    this.sendMessage(plugin.config.getString("prefix") + " $message")
}

fun CommandSender.msgTemplate(name: String, values: Map<String, Any> = mapOf()) {
    val template: String? = plugin.config.getString("messages.$name")
    var message: String

    template?.let {
        message = it
        values.forEach { entry -> message = message.replace("{${entry.key}}", entry.value.toString()) }
        msg(message)
    } ?: error("Template '${name}' not found!")
}

fun msgTemplateGlobal(name: String, values: Map<String, Any> = mapOf()) {
    val template: String? = plugin.config.getString("messages.$name")
    var message: String

    template?.let {
        message = it
        values.forEach { entry -> message = message.replace("{${entry.key}}", entry.value.toString()) }
        Bukkit.broadcastMessage(plugin.config.getString("prefix") + " $message")
    } ?: error("Template '${name}' not found!")
}
