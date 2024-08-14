package net.rebux.jumpandrun.events

import net.rebux.jumpandrun.parkour.Parkour
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/** An [Event] that is being called when a [player] joins a parkour */
class ParkourJoinEvent(val player: Player, val parkour: Parkour) : Event(), Cancellable {

    private var cancelled: Boolean = false

    @Override
    override fun getHandlers(): HandlerList {
        return HANDLERS_LIST
    }

    @Override
    override fun isCancelled(): Boolean {
        return cancelled
    }

    @Override
    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }

    companion object {
        private val HANDLERS_LIST = HandlerList()

        // Required from bukkit to find event handlers
        @Suppress("unused")
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS_LIST
        }
    }
}
