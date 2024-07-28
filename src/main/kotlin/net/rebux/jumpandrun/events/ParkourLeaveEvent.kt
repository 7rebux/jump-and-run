package net.rebux.jumpandrun.events

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * An [Event] that is being called when a [player] leaves parkour mode
 */
class ParkourLeaveEvent(val player: Player) : Event(), Cancellable {

  private var cancelled: Boolean = false

  override fun getHandlers(): HandlerList {
    return HANDLERS_LIST
  }

  override fun isCancelled(): Boolean {
    return cancelled
  }

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