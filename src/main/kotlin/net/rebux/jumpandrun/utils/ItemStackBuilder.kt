package net.rebux.jumpandrun.utils

import de.tr7zw.nbtapi.NBT
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

fun itemStack(material: Material, builder: ItemStack.() -> Unit) =
    ItemStack(material).apply(builder)

inline fun <reified T : ItemMeta> ItemStack.meta(builder: T.() -> Unit) {
    val curMeta = this.itemMeta as? T
    this.itemMeta = curMeta?.apply(builder) ?: itemMeta(this.type, builder)
}

@JvmName("regularMeta")
inline fun ItemStack.meta(builder: ItemMeta.() -> Unit) = meta<ItemMeta>(builder)

inline fun <reified T : ItemMeta> itemMeta(material: Material, builder: T.() -> Unit): T? {
    val meta = Bukkit.getItemFactory().getItemMeta(material)
    return if (meta is T) meta.apply(builder) else null
}

inline fun ItemMeta.lore(builder: MutableList<String>.() -> Unit) {
    lore = if (lore != null) {
        lore!!.apply(builder)
    } else {
        mutableListOf<String>().apply(builder)
    }
}

fun ItemStack.nbt(builder: ReadWriteItemNBT.() -> Unit) {
    NBT.modify(this, builder)
}
