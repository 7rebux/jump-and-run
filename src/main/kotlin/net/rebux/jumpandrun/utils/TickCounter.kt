package net.rebux.jumpandrun.utils

class TickCounter {
    var ticks = 0L
    var started = false

    fun start() {
        started = true
    }

    fun tick() {
        ticks++
    }

    fun pause() {
        started = false
    }

    fun stop(): Long {
        started = false
        return ticks.also { ticks = 0L }
    }
}
