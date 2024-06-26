package net.rebux.jumpandrun.utils

import java.util.concurrent.TimeUnit
import kotlin.math.abs

object TickFormatter {

  /**
   * Formats the given [ticks] to "(mm.)ss.SSS" and also returns the unit
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
}
