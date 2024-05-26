package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.Instance
import net.rebux.jumpandrun.data
import net.rebux.jumpandrun.item.Item
import net.rebux.jumpandrun.item.ItemRegistry
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object RestartItem : Item() {

    val id = ItemRegistry.register(this)

    // TODO: find a way to remove this
    private val plugin = Instance.plugin

    override fun createItemStack(): ItemStack {
        return Builder()
            .material(Material.REDSTONE)
            .displayName(plugin.config.getString("items.restart"))
            .build()
    }

    override fun onInteract(player: Player) {
        // Prevent restarting parkour when in practice mode
        if (player.data.isInPracticeMode()) {
            // TODO: message template
            player.sendMessage("Can't restart parkour while in practice mode")
            return
        }

        val startLocation = player.data.parkourData.parkour!!.location

        player.data.parkourData.checkpoint = startLocation
        player.data.parkourData.timer.stop()
        player.teleport(startLocation)
    }
}
