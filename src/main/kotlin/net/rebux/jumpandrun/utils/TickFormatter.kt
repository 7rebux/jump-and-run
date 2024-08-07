package net.rebux.jumpandrun.utils

import net.rebux.jumpandrun.config.MessagesConfig
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * An object with utility functions to format tick based values.
 *
 * 1 tick represents 1/20 of a second. A Minecraft server runs tick based, that means
 * this is the closest a time measurement can get on the server side.
 */
object TickFormatter {

  /**
   * Formats the given [ticks] to a pattern like <code>"(mm.)ss.SSS"</code>.
   *
   * @param[ticks] The ticks to format.
   * @return A pair of the formatted [String] and the [TimeUnit].
   */
  fun format(ticks: Long): Pair<String, TimeUnit> {
    val negative = ticks < 0
    val total = abs(ticks * 1000 / 20)
    val minutes = total / 60000
    val seconds = (total / 1000) % 60
    val millis = total % 1000
    val unit = if (minutes > 0) TimeUnit.MINUTES else TimeUnit.SECONDS

    val formatted = buildString {
      if (negative) {
        append("-")
      }

      if (unit == TimeUnit.MINUTES) {
        append(String.format("%02d", minutes))
        append(".")
      }

      append(String.format("%02d", seconds))
      append(".")
      append(String.format("%03d", millis))
    }

    return Pair(formatted, unit)
  }

  fun TimeUnit.toMessageValue() : String {
    return when (this) {
      TimeUnit.MINUTES -> MessagesConfig.Timer.Unit.minutes
      TimeUnit.SECONDS -> MessagesConfig.Timer.Unit.seconds
      else -> error("No message value for TimeUnit $this")
    }
  }
}
