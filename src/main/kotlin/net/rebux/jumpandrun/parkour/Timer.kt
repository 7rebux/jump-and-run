package net.rebux.jumpandrun.parkour

import org.bukkit.entity.Player
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import io.github.theluca98.textapi.ActionBar
import net.rebux.jumpandrun.utils.TimeUtil

class Timer(val player: Player) {
    private val executorService = Executors.newScheduledThreadPool(1)
    var elapsedMillis = 0
    private var stopped = true
    private val actionBar: ActionBar = ActionBar("")

    init {
        executorService.scheduleAtFixedRate({
            if (stopped)
                return@scheduleAtFixedRate

            actionBar.setText(TimeUtil.millisToTime(elapsedMillis))
            actionBar.send(player)
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