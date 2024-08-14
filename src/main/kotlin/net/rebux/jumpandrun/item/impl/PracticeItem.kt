package net.rebux.jumpandrun.item.impl

import net.rebux.jumpandrun.api.PlayerDataManager.data
import net.rebux.jumpandrun.item.Item
import net.rebux.jumpandrun.safeTeleport
import org.bukkit.entity.Player

object PracticeItem : Item("practice") {

    override fun onInteract(player: Player) {
        if (player.data.inPractice) {
            player.safeTeleport(player.data.practiceData.startLocation!!)
            player.data.practiceData.timer.stop()
        }
    }
}
