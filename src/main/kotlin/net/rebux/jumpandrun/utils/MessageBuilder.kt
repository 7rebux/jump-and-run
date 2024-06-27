package net.rebux.jumpandrun.utils

import net.rebux.jumpandrun.config.MessagesConfig
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

// TODO: Maybe use ComponentBuilder (Not sure if that is supported in 1.8?)
data class MessageBuilder(
  private var template: String? = null,
  private var values: Map<String, Any>? = null,
  private var prefix: Boolean = true,
  private var error: Boolean = false
) {

  fun template(template: String) = apply { this.template = template }

  fun values(values: Map<String, Any>) = apply { this.values = values }

  fun prefix(prefix: Boolean = true) = apply { this.prefix = prefix }

  fun error(error: Boolean = true) = apply { this.error = error }

  fun buildAndSend(receiver: CommandSender) {
    build().forEach(receiver::sendMessage)
  }

  fun buildAndSendGlobally() {
    build().forEach(Bukkit::broadcastMessage)
  }

  fun build(): List<String> {
    return template?.let {
      if (it.contains("\n")) {
        return it.split("\n")
          .map(::replaceValues)
          .map(::appendOptions)
      } else {
        return listOf(appendOptions(replaceValues(it)))
      }
    } ?: error("Message must be specified!")
  }

  private fun replaceValues(line: String): String {
    return values?.entries?.fold(line) { acc, (key, value) ->
      acc.replace("{$key}", value.toString())
    } ?: line
  }

  private fun appendOptions(line: String): String {
    return buildString {
      if (prefix) append(MessagesConfig.prefix)
      if (error) append(ChatColor.RED)
      append(line)
    }
  }
}
