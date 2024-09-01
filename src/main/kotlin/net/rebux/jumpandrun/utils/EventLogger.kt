package net.rebux.jumpandrun.utils

import net.rebux.jumpandrun.ParkourInstance
import net.rebux.jumpandrun.config.ParkourConfig

object EventLogger {

    val plugin = ParkourInstance.plugin

    fun log(key: String, message: String) {
        if (!ParkourConfig.eventLogging) {
            return
        }

        plugin.logger.info("[$key] $message")
    }

    fun warn(key: String, message: String) {
        if (!ParkourConfig.eventLogging) {
            return
        }

        plugin.logger.warning("[$key] $message")
    }
}