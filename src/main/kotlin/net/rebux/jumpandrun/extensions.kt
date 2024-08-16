package net.rebux.jumpandrun

import de.tr7zw.changeme.nbtapi.NBT
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun Player.safeTeleport(location: Location) {
    this.fallDistance = 0.0F
    this.teleport(location)
}

fun ItemStack.getTag(name: String): Int? {
    return NBT.get<Int?>(this) { nbt -> nbt.getOrNull(name, Integer.TYPE) }
}

fun <E> ItemStack.getEnumTag(name: String, enumClass: Class<E>): E? {
    return NBT.get<E?>(this) { nbt -> nbt.getOrNull(name, enumClass) }
}
