package net.rebux.jumpandrun

import de.tr7zw.changeme.nbtapi.NBT
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

// 1.8 Action Bar
//fun Player.sendActionBar(text: String) {
//    (this as CraftPlayer).handle.playerConnection.sendPacket(
//        PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"$text\"}"), 2)
//    )
//}

fun Player.safeTeleport(location: Location) {
  this.fallDistance = 0.0F
  this.teleport(location)
}

fun ItemStack.getTag(name: String): Int? {
  return NBT.get<Int?>(this) { nbt ->
    nbt.getOrNull(name, Integer.TYPE)
  }
}
