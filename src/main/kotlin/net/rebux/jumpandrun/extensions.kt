package net.rebux.jumpandrun

import de.tr7zw.nbtapi.NBT
import net.rebux.jumpandrun.listeners.PlayerMoveListener
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun Player.safeTeleport(location: Location) {
    this.fallDistance = 0.0F
    this.teleport(location)

    // Delete the last move packet from the cache to prevent false flags
    PlayerMoveListener.lastMoveLocation.remove(this)
}

fun ItemStack.getTag(name: String): Int? {
    return NBT.get<Int?>(this) { nbt -> nbt.getOrNull(name, Integer.TYPE) }
}

fun <E> ItemStack.getEnumTag(name: String, enumClass: Class<E>): E? {
    return NBT.get<E?>(this) { nbt -> nbt.getOrNull(name, enumClass) }
}
