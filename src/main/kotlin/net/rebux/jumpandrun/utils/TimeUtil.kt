package net.rebux.jumpandrun.utils

import net.rebux.jumpandrun.Instance

object TimeUtil {

    fun ticksToTime(ticks: Int): String {
        val millis = ticks * 1000 / 20

        val minutes: Int = (millis / 60000)
        val seconds: Int = (millis / 1000) % 60
        val milliseconds: Int = millis % 1000

        return "${String.format("%02d", minutes)}.${String.format("%02d", seconds)}.${String.format("%03d", milliseconds)} ${Instance.plugin.config.getString("messages.timeUnitMinutes")}"
    }
}
