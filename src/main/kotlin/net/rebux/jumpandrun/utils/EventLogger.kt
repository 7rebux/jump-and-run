package net.rebux.jumpandrun.utils

import net.rebux.jumpandrun.config.ParkourConfig

object EventLogger {

    fun log(key: String, message: String) {
        if (!ParkourConfig.eventLogging) {
            return
        }

        println("[$key] $message")
    }
}