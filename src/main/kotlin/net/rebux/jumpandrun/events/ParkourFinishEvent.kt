package net.rebux.jumpandrun.events

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * An [Event] that is being called when the [player] finishes a parkour
 */
class ParkourFinishEvent(val player: Player) : Event() {

    @Override
    override fun getHandlers(): HandlerList {
        return HANDLERS_LIST
    }

    companion object {
        private val HANDLERS_LIST = HandlerList()

        // required from bukkit to find event handlers
        @Suppress("unused")
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS_LIST
        }
    }
}
