package net.rebux.jumpandrun.api

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

data class PlayerStatePersistence(
    val player: Player,
    val inventory: Map<Int, ItemStack>,
    val gameMode: GameMode
) {

    fun restore() {
        player.gameMode = gameMode
        player.inventory.clear()
        inventory.forEach { (slot, itemStack) -> player.inventory.setItem(slot, itemStack) }
    }
}

fun Player.currentState(): PlayerStatePersistence {
    return PlayerStatePersistence(this, this.inventory.toMap(), this.gameMode)
}

private fun Inventory.toMap() = buildMap {
    val inventory = this@toMap

    for (i in 0..inventory.size) {
        if (inventory.getItem(i)?.type in listOf(null, Material.AIR)) {
            continue
        }

        this[i] = inventory.getItem(i)!!.clone()
    }
}
