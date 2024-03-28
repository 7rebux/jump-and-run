package net.rebux.jumpandrun.utils

class TickCounter {
    var ticks = 0
    var started = false

    fun start() {
        started = true
    }

    fun tick() {
        ticks++
    }

    fun stop(): Int {
        started = false
        return ticks
            .also { ticks = 0 }
    }
}
