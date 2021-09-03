package net.rebux.jumpandrun.utils

object TimeUtil {
    fun millisToTime(millis: Int): String {
        val minutes: Int = millis / 60000 % 60
        val seconds: Int = millis / 1000 % 60
        val milliseconds: Int = millis / 10 % 100

        return "${String.format("%02d", minutes)}:${String.format("%02d", seconds)}:${String.format("%02d", milliseconds)}"
    }
}