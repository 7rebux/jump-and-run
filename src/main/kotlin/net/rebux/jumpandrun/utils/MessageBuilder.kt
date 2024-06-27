package net.rebux.jumpandrun.utils

import net.rebux.jumpandrun.config.MessagesConfig
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

/**
 * A builder class for messages.
 *
 * This class is used to create any type of message in the plugin.
 * It also supports multiline messages by default.
 * A template is required to build a message.
 * The prefix is included by default.
 */
data class MessageBuilder(
  private var template: String? = null,
  private var values: Map<String, Any>? = null,
  private var prefix: Boolean = true,
  private var error: Boolean = false
) {

  /**
   * Specifies the template to use for the message. This is required.
   *
   * @param[template] The template to use as the message basis. This may include <code>\n</code> for multiple lines.
   */
  fun template(template: String) = apply { this.template = template }

  /**
   * Specifies the values to be set in the message.
   *
   * @param[values] A map of key value pairs which replaces each key in the template with its value.
   */
  fun values(values: Map<String, Any>) = apply { this.values = values }

  /**
   * Specifies whether the message should start with the default prefix.
   * This is true by default.
   *
   * @param[prefix] Whether the message should start with the prefix.
   */
  fun prefix(prefix: Boolean = true) = apply { this.prefix = prefix }

  /**
   * Specifies whether the message should be formatted as an error.
   *
   * @param[error] Whether the message should be formatted as an error.
   */
  fun error(error: Boolean = true) = apply { this.error = error }

  /**
   * Builds the message and returns all lines in a [Collection].
   */
  fun build(): Collection<String> {
    return template?.let {
      if (it.contains("\n")) {
        return it.split("\n")
          .map(::replaceValues)
          .map(::appendOptions)
      } else {
        return listOf(appendOptions(replaceValues(it)))
      }
    } ?: error("Template must be specified!")
  }

  /**
   * Builds the message and only returns the first line.
   */
  fun buildSingle(): String {
    return build().first()
  }

  /**
   * Builds and sends all lines of the message to the provided [receiver].
   *
   * @param[receiver] The receiver of the message.
   */
  fun buildAndSend(receiver: CommandSender) {
    build().forEach(receiver::sendMessage)
  }

  /**
   * Builds the message and sends all lines as a broadcast message to the server.
   */
  fun buildAndSendGlobally() {
    build().forEach(Bukkit::broadcastMessage)
  }

  private fun replaceValues(line: String): String {
    return values?.entries?.fold(line) { acc, (key, value) ->
      acc.replace("{$key}", value.toString())
    } ?: line
  }

  private fun appendOptions(line: String): String {
    return buildString {
      if (prefix) append(MessagesConfig.prefix)
      if (error) append("§4")
      append(line)
    }
  }
}
