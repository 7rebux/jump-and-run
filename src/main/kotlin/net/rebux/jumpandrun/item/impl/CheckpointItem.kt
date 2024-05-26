package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.data
import net.rebux.jumpandrun.item.Item
import net.rebux.jumpandrun.item.ItemRegistry
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object CheckpointItem : Item() {

    val id = ItemRegistry.register(this)

    override fun createItemStack(): ItemStack {
        return Builder()
            .material(Material.INK_SACK)
            .durability(1)
            .displayName(Instance.plugin.config.getString("items.checkpoint"))
            .build()
    }

    override fun onInteract(player: Player) {
        if (player.data.isInParkour() && !player.data.isInPracticeMode()) {
            player.teleport(player.data.parkourData.checkpoint)
        } else if (player.data.isInPracticeMode()) {
            player.teleport(player.data.practiceData.startLocation)
            player.data.practiceData.timer.stop()
        }
    }
}
