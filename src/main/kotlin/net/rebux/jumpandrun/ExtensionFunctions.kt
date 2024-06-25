package net.rebux.jumpandrun

import de.tr7zw.changeme.nbtapi.NBT
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.logging.Level

private val plugin = Instance.plugin

fun template(name: String, values: Map<String, Any> = mapOf()): String {
  val template: String? = plugin.config.getString(name)
  var message: String

  template?.let {
    message = it
    values.forEach { entry -> message = message.replace("{${entry.key}}", entry.value.toString()) }
    return message
  } ?: plugin.logger.log(Level.SEVERE, "Template '${name}' not found!\"")

  return "${ChatColor.RED}Not found"
}


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
