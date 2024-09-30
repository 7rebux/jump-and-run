package net.rebux.jumpandrun.listeners

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.config.MessagesConfig
import net.rebux.jumpandrun.config.ParkourConfig
import net.rebux.jumpandrun.config.SoundsConfig
import net.rebux.jumpandrun.events.ParkourFinishEvent
import net.rebux.jumpandrun.events.PracticeFinishEvent
import net.rebux.jumpandrun.parkour.Parkour
import net.rebux.jumpandrun.safeTeleport
import net.rebux.jumpandrun.utils.ActionBarUtil.sendActionBar
import net.rebux.jumpandrun.utils.EventLogger
import net.rebux.jumpandrun.utils.MessageBuilder
import net.rebux.jumpandrun.utils.SoundUtil
import net.rebux.jumpandrun.utils.TickFormatter
import net.rebux.jumpandrun.utils.TickFormatter.toMessageValue
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import kotlin.math.min

object PlayerMoveListener : Listener {

    // TODO:    In the future we could keep track of all the move packets in a "run".
    //          That way we don't have to reset the last location on teleports.
    //          And also have a validation method which checks if a run is valid.
    // We keep track of the destination from the last move packet in order to detect potential packet loss
    val lastMoveLocation = mutableMapOf<Player, Location?>()

    // This event is manipulated through a custom server jar file to be called every tick,
    // regardless if the player moved or not.
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player
        val data = player.data
        val blockBelow = player.location.block.getRelative(BlockFace.DOWN)

        if (!data.inParkour && !data.inPractice) {
            return
        }

        // Determines the fallback position to teleport the player to when he fails
        val checkpoint =
            if (data.inPractice) data.practiceData.startLocation
                ?: error("PracticeData of ${player.name} is missing startLocation")
            else data .parkourData.checkpoint
                ?: error("ParkourData of ${player.name} is missing checkpoint")

        handleTimer(player, event.isPositionChange())

        if (!data.inPractice) {
            checkForPacketLoss(player, event)
            handleParkourSplits(player, blockBelow)
        }

        if (player.location.y <= ParkourConfig.resetHeight) {
            player.safeTeleport(checkpoint)
            SoundUtil.playSound(SoundsConfig.resetHeight, player)
            return
        }

        // Special blocks
        when (blockBelow.type) {
            ParkourConfig.Block.reset -> handleReset(player, checkpoint)
            ParkourConfig.Block.checkpoint -> handleCheckpoint(player, player.location.block.location)
            else -> {}
        }

        // Handle finish
        if (blockBelow.isFinishBlockFor(data.parkourData.parkour!!) && !player.data.inPractice) {
            Bukkit.getPluginManager().callEvent(ParkourFinishEvent(player, player.data.parkourData.parkour!!))
        } else if (data.inPractice && blockBelow.location == data.practiceData.finishPosition) {
            Bukkit.getPluginManager().callEvent(PracticeFinishEvent(player))
        }
    }

    private fun handleParkourSplits(player: Player, blockBelow: Block) {
        val split = player.data.parkourData.splits.firstOrNull { it.block == blockBelow }
            ?: return
        val splitIndex = player.data.parkourData.splits.indexOf(split)
        val elapsedTicks = player.data.parkourData.timer.ticks

        if (split.reached) {
            return
        }

        if (split.bestTime == null) {
            split.bestTime = elapsedTicks
            return
        }

        val ticksDelta = split.bestTime!! - elapsedTicks
        val delta = TickFormatter.format(ticksDelta).first
        val color = when {
            ticksDelta > 0 -> ChatColor.GREEN
            ticksDelta < 0 -> ChatColor.RED
            else -> ChatColor.GRAY
        }

        split.bestTime = min(split.bestTime!!, elapsedTicks)
        split.reached = true

        player.sendMessage("$splitIndex: $color$delta")
    }

    private fun checkForPacketLoss(player: Player, event: PlayerMoveEvent) {
        lastMoveLocation[player]?.run {
            if (this != event.from) {
                EventLogger.warn(
                    "PlayerMoveEvent",
                    "Detected packet loss for player ${player.name} (Parkour=${player.data.parkourData.parkour?.id})"
                )
            }
        }
        lastMoveLocation[player] = event.to
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

        // TODO: Show this in a different color for practice mode
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
        if (!ParkourConfig.Feature.checkpoint || player.data.inPractice) {
            return
        }

        // Make sure checkpoint is not already set
        // TODO: Not a clean solution with the distance check, why is this even needed?
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

/**
 * Checks whether the [Block] is the finish location for the specified [parkour].
 */
private fun Block.isFinishBlockFor(parkour: Parkour) =
    if (parkour.finishLocation == null) type == ParkourConfig.Block.finish
    else location == parkour.finishLocation

/**
 * Checks if at least one of the x, y or z position has changed compared to the last tick.
 */
private fun PlayerMoveEvent.isPositionChange() =
    to?.let { from.x != it.x || from.y != it.y || from.z != it.z } ?: false

/**
 * Takes any location and returns the top middle of the closest block.
 */
private fun Location.normalized() =
    this.add(if (x < 0) -0.5 else 0.5, 0.0, if (z < 0) -0.5 else 0.5)
