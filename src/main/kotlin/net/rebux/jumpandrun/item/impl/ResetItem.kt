package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.item.Item
import net.rebux.jumpandrun.safeTeleport
import org.bukkit.entity.Player

object ResetItem : Item("reset") {

    override fun onInteract(player: Player) {
        if (player.data.inParkour) {
            player.safeTeleport(player.data.parkourData.checkpoint!!)
        }
    }
}
