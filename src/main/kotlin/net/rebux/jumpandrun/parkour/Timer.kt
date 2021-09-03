package net.rebux.jumpandrun.parkour

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Timer {
    private val executorService = Executors.newScheduledThreadPool(1)
    var elapsedMillis = 0
    private var stopped = true

    init {
        executorService.scheduleAtFixedRate({
            if (stopped)
                return@scheduleAtFixedRate

            elapsedMillis++

        },0, 1, TimeUnit.MILLISECONDS)
    }

    fun stop() {
        stopped = true
    }

    fun start() {
        stopped = false
        elapsedMillis = 0
    }
}