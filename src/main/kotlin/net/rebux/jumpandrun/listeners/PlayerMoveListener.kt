package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.MessagesConfig
import net.rebux.jumpandrun.config.ParkourConfig
import net.rebux.jumpandrun.config.SoundsConfig
import net.rebux.jumpandrun.events.ParkourFinishEvent
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.safeTeleport
import net.rebux.jumpandrun.utils.ActionBarUtil.sendActionBar
import net.rebux.jumpandrun.utils.EventLogger
import net.rebux.jumpandrun.utils.MessageBuilder
import net.rebux.jumpandrun.utils.SoundUtil
import net.rebux.jumpandrun.utils.TickFormatter
import net.rebux.jumpandrun.utils.TickFormatter.toMessageValue
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

object PlayerMoveListener : Listener {

    private val lastMoveLocation = mutableMapOf<Player, Location?>()

    // This event is manipulated through a custom server jar file to be called every tick,
    // regardless if the player moved or not.
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player
        val data = event.player.data
        val blockBelow = player.location.block.getRelative(BlockFace.DOWN)

        if (!data.inParkour && !data.inPractice) {
            return
        }

        val checkpoint =
            if (data.inPractice) data.practiceData.startLocation!!
            else data.parkourData.checkpoint!!

        handleTimer(player, event.hasPositionChanged())

        // Check for packet loss
        if (data.inParkour) {
            lastMoveLocation[player]?.run {
                if (this != event.from) {
                    EventLogger.warn(
                        "PlayerMoveEvent",
                        "Detected packet loss for player ${player.name}"
                    )
                }
            }
            lastMoveLocation[player] = event.to
        }

        // Handle reset height
        if (player.location.y <= ParkourConfig.resetHeight) {
            player.safeTeleport(checkpoint)
            SoundUtil.playSound(SoundsConfig.resetHeight, player)
            return
        }

        // Handle finish
        if (blockBelow.isFinishBlockFor(data.parkourData.parkour!!) && !player.data.inPractice) {
            Bukkit.getPluginManager()
                .callEvent(ParkourFinishEvent(player, player.data.parkourData.parkour!!))
            return
        }

        when (blockBelow.type) {
            ParkourConfig.Block.reset -> handleReset(player, checkpoint)
            ParkourConfig.Block.checkpoint ->
                handleCheckpoint(player, player.location.block.location)
            else -> {}
        }
    }

    private fun handleTimer(player: Player, hasMoved: Boolean) {
        val timer =
            if (player.data.inPractice) player.data.practiceData.timer
            else player.data.parkourData.timer

        if (!timer.started && hasMoved) {
            timer.start()
            SoundUtil.playSound(SoundsConfig.timerStart, player)
        }

        if (timer.started) {
            timer.tick()
        }

        // TODO: Maybe show this in a different color for practice mode
        val (time, unit) = TickFormatter.format(timer.ticks)
        player.sendActionBar(
            MessageBuilder(MessagesConfig.Timer.bar)
                .values(mapOf("time" to time, "unit" to unit.toMessageValue()))
                .prefix(false)
                .buildSingle())
    }

    private fun handleReset(player: Player, lastLocation: Location) {
        if (!ParkourConfig.Feature.resetBlock) {
            return
        }

        player.safeTeleport(lastLocation)
        MessageBuilder(MessagesConfig.Event.resetBlock).buildAndSend(player)
        SoundUtil.playSound(SoundsConfig.resetBlock, player)
    }

    private fun handleCheckpoint(player: Player, location: Location) {
        if (!ParkourConfig.Feature.checkpoint) {
            return
        }

        // We ignore checkpoints in practice mode
        if (player.data.inPractice) {
            return
        }

        // Make sure checkpoint is not already set
        // TODO: Not a clean solution with the distance check
        if (player.data.parkourData.checkpoint!!.distance(location) < 2.0) {
            return
        }

        player.data.parkourData.checkpoint =
            location.normalized().apply {
                this.yaw = player.location.yaw
                this.pitch = player.location.pitch
            }
        MessageBuilder(MessagesConfig.Event.checkpoint).buildAndSend(player)
        SoundUtil.playSound(SoundsConfig.checkpoint, player)
    }
}

private fun Block.isFinishBlockFor(parkour: Parkour) =
    if (parkour.finishLocation == null) type == ParkourConfig.Block.finish
    else location == parkour.finishLocation

private fun PlayerMoveEvent.hasPositionChanged() =
    to?.let { from.x != it.x || from.y != it.y || from.z != it.z } ?: false

private fun Location.normalized() =
    this.add(if (x < 0) -0.5 else 0.5, 0.0, if (z < 0) -0.5 else 0.5)
