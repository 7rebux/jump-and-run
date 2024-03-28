package net.rebux.jumpandrun.utils

import net.rebux.jumpandrun.template

object TimeUtil {
    fun ticksToTime(ticks: Int): String {
        val millis = ticks * 1000 / 20

        val m = (millis / 60000)
        val s = (millis / 1000) % 60
        val ms = millis % 1000

        return if (m == 0)
            "${String.format("%02d", s)}.${String.format("%03d", ms)} ${template("timer.units.seconds")}"
        else
            "${String.format("%02d", m)}.${String.format("%02d", s)}.${String.format("%03d", ms)} ${template("timer.units.minutes")}"
    }
}
